package com.parcelflow.infrastructure.extraction;

import com.parcelflow.domain.model.ParcelMetadata;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class VintedGoExtractionAdapterTest {

    private final VintedGoExtractionAdapter adapter = new VintedGoExtractionAdapter();

    @Test
    void shouldExtractValidVintedGoEmail() {
        String html = """
            <html>
            <body>
            ... Il est temps de récupérer ton colis ! ...
            saisis le code suivant : <b>E32782</b>
            retirer avant le</div><div><b>17/12/2025</b>
            Numéro de suivi : <a href="...">1764156123430443</a>
            <div class="block-header">Adresse</div>
            <div>Les Casiers Des Saveurs</div>
            </body>
            </html>
            """;
        
        Optional<ParcelMetadata> result = adapter.extract(html, ZonedDateTime.now());
        
        assertTrue(result.isPresent(), "Extraction failed");
        ParcelMetadata meta = result.get();
        assertEquals("Vinted Go", meta.carrier());
        assertEquals("E32782", meta.pickupCode());
        assertEquals(LocalDate.of(2025, 12, 17), meta.expirationDate());
        assertEquals("1764156123430443", meta.trackingCode());
        assertTrue(meta.pickupLocation().contains("Les Casiers Des Saveurs"));
    }

    @Test
    void shouldReturnEmptyForNonVintedEmail() {
        Optional<ParcelMetadata> result = adapter.extract("Hello world", ZonedDateTime.now());
        assertTrue(result.isEmpty());
    }
}
