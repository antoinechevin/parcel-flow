package com.parcelflow.infrastructure.extraction;

import com.parcelflow.domain.model.ParcelMetadata;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.Part;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class MondialRelayExtractionAdapterTest {

    private final MondialRelayExtractionAdapter adapter = new MondialRelayExtractionAdapter();

    @Test
    void shouldExtractMetadataFromMondialRelayEmail() throws IOException, MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        
        String htmlContent;
        ZonedDateTime receivedAt = ZonedDateTime.parse("2025-12-05T03:25:49-08:00");
        
        try (InputStream is = new ClassPathResource("emails/mail_mondial_relay.eml").getInputStream()) {
            MimeMessage message = new MimeMessage(session, is);
            htmlContent = extractHtml(message);
        }

        assertNotNull(htmlContent, "HTML content should not be null");

        Optional<ParcelMetadata> result = adapter.extract(htmlContent, receivedAt);

        assertTrue(result.isPresent(), "Should extract metadata from Mondial Relay email");
        ParcelMetadata metadata = result.get();

        assertEquals("44795167", metadata.trackingCode());
        assertEquals("Mondial Relay", metadata.carrier());
        // 5 days after Dec 5 is Dec 10
        assertEquals(LocalDate.of(2025, 12, 10), metadata.expirationDate());
        assertEquals("LOCKER 24/7 LA CERISE BLEUE BESSENAY", metadata.pickupLocation());
    }

    private String extractHtml(Part part) throws MessagingException, IOException {
        if (part.isMimeType("text/html")) {
            return (String) part.getContent();
        }
        if (part.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                String result = extractHtml(multipart.getBodyPart(i));
                if (result != null) return result;
            }
        }
        return null;
    }
}
