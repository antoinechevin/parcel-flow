package com.parcelflow.infrastructure.mail;

import com.parcelflow.domain.model.InboundEmail;
import com.parcelflow.domain.model.MailFetchResult;
import com.parcelflow.domain.ports.MailSourcePort;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PreviewMailSourceDecorator implements MailSourcePort {

    private static final Logger log = LoggerFactory.getLogger(PreviewMailSourceDecorator.class);
    private final MailSourcePort delegate;

    private boolean injectedMockOnce = false;

    public PreviewMailSourceDecorator(MailSourcePort delegate) {
        this.delegate = delegate;
        log.info("PreviewMailSourceDecorator activated. Mock emails will be injected alongside real emails.");
    }

    @Override
    public MailFetchResult fetchEmails(ZonedDateTime since, String query) {
        // 1. Fetch real emails via the delegate (Gmail)
        MailFetchResult realResult = delegate.fetchEmails(since, query);
        List<InboundEmail> combinedEmails = new ArrayList<>(realResult.emails());

        // 2. Fetch and inject mock emails only once per JVM startup/polling run cycle to avoid spamming
        // Since EmailPollingOrchestrator loops per provider, we limit the logging and overhead.
        if (!injectedMockOnce) {
            try {
                List<InboundEmail> mockEmails = loadMockEmails();
                combinedEmails.addAll(mockEmails);
                log.info("Injected {} mock emails into fetch results", mockEmails.size());
                injectedMockOnce = true;
            } catch (Exception e) {
                log.error("Failed to load mock emails. Proceeding with real emails only.", e);
            }
        } else {
            // We still need to return them on subsequent calls if the orchestrator loops,
            // but the orchestrator calls this for EACH provider and processes them all anyway.
            // Wait, if we don't return them on the 2nd provider, the 2nd provider won't see its mock email!
            // Let's just suppress the log, but keep returning the list.
            try {
                List<InboundEmail> mockEmails = loadMockEmails();
                combinedEmails.addAll(mockEmails);
            } catch (Exception e) {
                // silent
            }
        }

        // Return combined list, keeping the real watermark
        return new MailFetchResult(combinedEmails, realResult.newWatermark());
    }

    private List<InboundEmail> loadMockEmails() throws IOException {
        List<InboundEmail> mockEmails = new ArrayList<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:preview-emails/*.eml");

        Session mailSession = Session.getDefaultInstance(new Properties());

        for (Resource resource : resources) {
            try (InputStream is = resource.getInputStream()) {
                MimeMessage mimeMessage = new MimeMessage(mailSession, is);
                InboundEmail mockEmail = mapToDomainAndMock(mimeMessage, resource.getFilename());
                if (mockEmail != null) {
                    mockEmails.add(mockEmail);
                }
            } catch (MessagingException e) {
                log.warn("Failed to parse mock email: {}", resource.getFilename(), e);
            }
        }
        return mockEmails;
    }

    private InboundEmail mapToDomainAndMock(MimeMessage msg, String filename) throws MessagingException, IOException {
        String id = "MOCK-" + filename;
        String subject = "[MOCK] " + (msg.getSubject() != null ? msg.getSubject() : "No Subject");
        String sender = msg.getFrom() != null && msg.getFrom().length > 0 ? msg.getFrom()[0].toString() : "unknown@mock.com";

        // Force reception date to NOW so packages are not expired
        ZonedDateTime receivedAt = ZonedDateTime.now();

        // To be absolutely robust, we use placeholders directly in the .eml files.
        // Read the body, but note that reading via MimeMessage extracts the decoded text.
        // If the placeholders were in the original file, they should survive here.
        String rawBody = extractBody(msg);

        String mockedBody = mockTrackingNumberInBody(rawBody);

        return new InboundEmail(id, subject, mockedBody, sender, receivedAt);
    }

    private String extractBody(jakarta.mail.Part part) throws MessagingException, IOException {
        if (part.isMimeType("text/plain") || part.isMimeType("text/html")) {
            return part.getContent().toString();
        } else if (part.isMimeType("multipart/*")) {
            jakarta.mail.internet.MimeMultipart multipart = (jakarta.mail.internet.MimeMultipart) part.getContent();
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < multipart.getCount(); i++) {
                String subPart = extractBody(multipart.getBodyPart(i));
                if (subPart != null) {
                    // Prefer HTML if available
                    if (multipart.getBodyPart(i).isMimeType("text/html")) {
                        return subPart;
                    }
                    result.append(subPart);
                }
            }
            return result.toString();
        }
        return "";
    }

    private String mockTrackingNumberInBody(String body) {
        if (body == null) return null;
        String mocked = body;

        // 1. Replace hardcoded placeholders we manually put in the .eml files
        DateTimeFormatter frenchFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);
        String futureDateStr = ZonedDateTime.now().plusDays(5).format(frenchFormatter);
        mocked = mocked.replace("{{FUTURE_DATE_CHRONO}}", futureDateStr);

        DateTimeFormatter slashFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String futureSlashDateStr = ZonedDateTime.now().plusDays(5).format(slashFormatter);
        mocked = mocked.replace("{{FUTURE_DATE_VINTED}}", futureSlashDateStr);

        // 2. Add visual marker
        if (!mocked.contains("data-mock")) {
             mocked = mocked.replace("<body", "<body data-mock=\"true\"");
        }

        return mocked;
    }
}
