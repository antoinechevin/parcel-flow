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

    // Regex for dynamic tracking code replacements
    private static final Pattern MONDIAL_RELAY_TRACKING = Pattern.compile("(Votre colis\\s+)(\\d+)(\\s+est disponible)");
    private static final Pattern MONDIAL_RELAY_HTML_TRACKING = Pattern.compile("(<strong>)(\\d+)(</strong>)");
    private static final Pattern CHRONOPOST_TRACKING = Pattern.compile("(n°(?:\\s*(?:de colis)?\\s*<[^>]+>\\s*|\\s+))([A-Z0-9]{10,20})", Pattern.CASE_INSENSITIVE);
    private static final Pattern VINTED_GO_TRACKING = Pattern.compile("(numéro de suivi(?:\\s*<[^>]+>\\s*)*\\s*)([A-Z0-9]{10,20})", Pattern.CASE_INSENSITIVE);
    private static final Pattern EXPIRATION_DATE_PATTERN = Pattern.compile("(jusqu'au\\s+(?:<[^>]+>)*)\\s*[a-zA-Z]+\\s+\\d+\\s+[a-zA-Z]+\\s+\\d{4}");

    public PreviewMailSourceDecorator(MailSourcePort delegate) {
        this.delegate = delegate;
        log.info("PreviewMailSourceDecorator activated. Mock emails will be injected alongside real emails.");
    }

    @Override
    public MailFetchResult fetchEmails(ZonedDateTime since, String query) {
        // 1. Fetch real emails via the delegate (Gmail)
        MailFetchResult realResult = delegate.fetchEmails(since, query);
        List<InboundEmail> combinedEmails = new ArrayList<>(realResult.emails());

        // 2. Fetch and inject mock emails from classpath
        try {
            List<InboundEmail> mockEmails = loadMockEmails();
            combinedEmails.addAll(mockEmails);
            log.info("Injected {} mock emails into fetch results", mockEmails.size());
        } catch (Exception e) {
            log.error("Failed to load mock emails. Proceeding with real emails only.", e);
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

        String rawBody = extractBody(msg);

        // Mock the tracking number to visually indicate it's a test package
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

        // Force a future expiration date for Chronopost/Vinted
        DateTimeFormatter frenchFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);
        String futureDateStr = ZonedDateTime.now().plusDays(5).format(frenchFormatter);
        Matcher dateMatcher = EXPIRATION_DATE_PATTERN.matcher(mocked);
        if (dateMatcher.find()) {
            // Replace the date part while keeping the "jusqu'au" and any HTML tags intact
            mocked = dateMatcher.replaceAll("$1 " + futureDateStr);
        }

        // Try to replace numbers for Mondial Relay
        Matcher m1 = MONDIAL_RELAY_TRACKING.matcher(mocked);
        if (m1.find()) {
            mocked = m1.replaceAll("$1MOCK-$2$3");
        }
        Matcher m2 = MONDIAL_RELAY_HTML_TRACKING.matcher(mocked);
        if (m2.find()) {
            mocked = m2.replaceAll("$1MOCK-$2$3");
        }

        // Try Chronopost
        Matcher m3 = CHRONOPOST_TRACKING.matcher(mocked);
        if (m3.find()) {
             mocked = m3.replaceAll("$1MOCK-$2");
        }

        // Try Vinted
        Matcher m4 = VINTED_GO_TRACKING.matcher(mocked);
        if (m4.find()) {
             mocked = m4.replaceAll("$1MOCK-$2");
        }

        // Add a global visual marker if no regex matched perfectly but we know it's a mock
        if (mocked.equals(body)) {
             mocked = body.replace("<body", "<body data-mock=\"true\"");
        }

        return mocked;
    }
}
