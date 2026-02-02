package com.parcelflow.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ParcelTest {

    @Test
    void shouldCreateParcelWithCarrier() {
        Parcel parcel = new Parcel(
            new ParcelId(UUID.randomUUID()),
            "1Z999",
            "UPS",
            LocalDate.now(),
            ParcelStatus.AVAILABLE,
            new PickupPoint("id", "Name", "Address", "Hours"),
            "1234",
            "http://qr.url",
            BarcodeType.QR_CODE
        );

        assertEquals("UPS", parcel.carrier());
        assertEquals(BarcodeType.QR_CODE, parcel.barcodeType());
    }
}
