package com.parcelflow.infrastructure.adapters.extraction.chronopost;

import com.parcelflow.domain.model.ParcelMetadata;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ChronopostReroutingStrategyTest {

    private final ChronopostReroutingStrategy strategy = new ChronopostReroutingStrategy();

    @Test
    void shouldExtractMetadataFromReroutingEmail() throws IOException {
        String htmlContent;
        try (InputStream is = new ClassPathResource("emails/mail_redirection_relais.txt").getInputStream()) {
            htmlContent = new String(is.readAllBytes());
        }

        Optional<ParcelMetadata> result = strategy.extract(htmlContent, java.time.ZonedDateTime.now());

        assertTrue(result.isPresent(), "Should extract metadata from rerouting email");
        ParcelMetadata metadata = result.get();

        System.out.println("DEBUG: Extracted Pickup Location: [" + metadata.pickupLocation() + "]");

        assertEquals("XW313763680TS", metadata.trackingCode());
        assertEquals("Chronopost (Rerouté)", metadata.carrier());
        assertEquals("Consigne Otarie L Arbresle", metadata.pickupLocation());
        assertEquals("6315 8994", metadata.pickupCode());
        assertNotNull(metadata.expirationDate());
        assertEquals(2026, metadata.expirationDate().getYear());
        assertEquals(3, metadata.expirationDate().getMonthValue());
        assertEquals(3, metadata.expirationDate().getDayOfMonth());
    }
}
