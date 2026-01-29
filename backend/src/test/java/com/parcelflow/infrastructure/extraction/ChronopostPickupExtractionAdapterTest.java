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
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ChronopostPickupExtractionAdapterTest {

    private final ChronopostPickupExtractionAdapter adapter = new ChronopostPickupExtractionAdapter();

    @Test
    void shouldExtractMetadataFromRealChronopostEmail() throws IOException, MessagingException {
        // Load and parse the real sample email using Jakarta Mail to decode it properly
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        
        String htmlContent;
        try (InputStream is = new ClassPathResource("emails/mail_chronopost.eml").getInputStream()) {
            MimeMessage message = new MimeMessage(session, is);
            htmlContent = extractHtml(message);
        }

        assertNotNull(htmlContent, "HTML content should not be null");

        Optional<ParcelMetadata> result = adapter.extract(htmlContent, java.time.ZonedDateTime.now());

        assertTrue(result.isPresent(), "Should extract metadata from Chronopost email");
        ParcelMetadata metadata = result.get();

        // Verify Tracking Number
        assertEquals("XW251575070TS", metadata.trackingCode(), "Tracking code mismatch");

        // Verify Carrier
        assertTrue(metadata.carrier().contains("Vinted"), "Carrier should be Vinted (Chronopost)");

        // Verify Expiration Date
        // "lundi 19 janvier 2026" -> 2026-01-19
        assertEquals(LocalDate.of(2026, 1, 19), metadata.expirationDate(), "Expiration date mismatch");

        // Verify Pickup Location
        assertNotNull(metadata.pickupLocation());
        assertTrue(metadata.pickupLocation().contains("Panier Sympa"), "Pickup location should contain 'Panier Sympa'");

        // Verify Pickup Code
        assertEquals("611553", metadata.pickupCode(), "Pickup code mismatch");

        // Verify QR Code URL
        assertNotNull(metadata.qrCodeUrl());
        assertTrue(metadata.qrCodeUrl().contains("AztecCode"), "QR Code URL should contain AztecCode");
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

    @Test
    void shouldExtractTrackingNumberWithPrefixStrategy() {
        String html = "<html><body>Votre colis VINTED n° XW277558241TS est disponible dans votre relais Pickup Chronopost !</body></html>";
        Optional<ParcelMetadata> result = adapter.extract(html, java.time.ZonedDateTime.now());
        
        assertTrue(result.isPresent());
        assertEquals("XW277558241TS", result.get().trackingCode());
    }
}
