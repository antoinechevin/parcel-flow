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
            "UPS",
            tomorrow,
            "Relais Colis",
            "1234",
            "http://qr.url",
            BarcodeType.QR_CODE
        );

        assertEquals("1Z999", metadata.trackingCode());
        assertEquals("UPS", metadata.carrier());
        assertEquals(tomorrow, metadata.expirationDate());
        assertEquals("Relais Colis", metadata.pickupLocation());
        assertEquals("1234", metadata.pickupCode());
        assertEquals("http://qr.url", metadata.qrCodeUrl());
        assertEquals(BarcodeType.QR_CODE, metadata.barcodeType());
    }
}