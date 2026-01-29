package com.parcelflow.infrastructure.extraction;

import com.parcelflow.domain.model.ParcelMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class VintedGoExtractionAdapterTest {

    private final VintedGoExtractionAdapter adapter = new VintedGoExtractionAdapter();

    @Test
    void shouldExtractMetadataFromVintedGoEmail() throws IOException {
        ClassPathResource resource = new ClassPathResource("emails/mail_vinted_go.eml");
        String rawContent = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        
        // Simuler le décodage Quoted-Printable que ferait le client Gmail
        String decodedContent = rawContent.replaceAll("=\\r?\\n", "")
                                          .replaceAll("=3D", "=")
                                          .replaceAll("=C3=A9", "é")
                                          .replaceAll("=C3=A8", "è")
                                          .replaceAll("=C3=A0", "à")
                                          .replaceAll("=C3=80", "À")
                                          .replaceAll("=E2=80=93", "–")
                                          .replaceAll("=E2=82=AC", "€");

        Optional<ParcelMetadata> result = adapter.extract(decodedContent, ZonedDateTime.now());

        assertTrue(result.isPresent(), "Should have extracted metadata");
        ParcelMetadata metadata = result.get();

        assertEquals("1764156123430443", metadata.trackingCode());
        assertEquals("Vinted Go", metadata.carrier());
        assertEquals(LocalDate.of(2025, 12, 17), metadata.expirationDate());
        assertTrue(metadata.pickupLocation().contains("Les Casiers Des Saveurs"));
    }
}
