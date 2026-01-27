package com.parcelflow.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class ParcelMetadataTest {

    @Test
    void shouldCreateParcelMetadata() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        ParcelMetadata metadata = new ParcelMetadata(
            "1Z999",
            null, // pickupCode
            "UPS",
            tomorrow,
            "Relais Colis"
        );

        assertEquals("1Z999", metadata.trackingCode());
        assertEquals("UPS", metadata.carrier());
        assertEquals(tomorrow, metadata.expirationDate());
        assertEquals("Relais Colis", metadata.pickupLocation());
    }
}