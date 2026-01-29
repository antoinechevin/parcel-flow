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
        
        // New field
        assertEquals("887498", metadata.pickupCode());
        assertNull(metadata.qrCodeUrl());
    }

    @Test
    void shouldExtractMetadataFromStandardPickupEmail() throws IOException, MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        
        String htmlContent;
        ZonedDateTime receivedAt = ZonedDateTime.parse("2026-01-27T13:23:31Z");
        
        try (InputStream is = new ClassPathResource("emails/mail_mondial_relay_2.eml").getInputStream()) {
            MimeMessage message = new MimeMessage(session, is);
            htmlContent = extractHtml(message);
        }

        assertNotNull(htmlContent, "HTML content should not be null");

        Optional<ParcelMetadata> result = adapter.extract(htmlContent, receivedAt);

        assertTrue(result.isPresent(), "Should extract metadata from standard Mondial Relay email");
        ParcelMetadata metadata = result.get();

        assertEquals("08730269", metadata.trackingCode());
        assertEquals("Mondial Relay", metadata.carrier());
        assertEquals(LocalDate.of(2026, 2, 1), metadata.expirationDate());
        assertEquals("CAMINHOS DE PORTUGAL SAIN-BEL", metadata.pickupLocation());
    }

    @Test
    void shouldExtractMetadataUsingRegexFallback() {
        String htmlContent = "<html><body>" +
                "Votre colis 99999999 est disponible dans votre Point Relais<sup>®</sup> " +
                "<span style=\"color: #FF5C84;\"> TEST FALLBACK POINT </span> - Adresse..." +
                "FAITES VITE, DANS 3 JOURS VOTRE COLIS REPARTIRA !" +
                "Mondial Relay" +
                "</body></html>";
        ZonedDateTime receivedAt = ZonedDateTime.parse("2026-01-20T10:00:00Z");

        Optional<ParcelMetadata> result = adapter.extract(htmlContent, receivedAt);

        assertTrue(result.isPresent());
        ParcelMetadata metadata = result.get();
        assertEquals("99999999", metadata.trackingCode());
        assertEquals("TEST FALLBACK POINT", metadata.pickupLocation());
        assertEquals(LocalDate.of(2026, 1, 23), metadata.expirationDate());
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
