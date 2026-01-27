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
            null,
            "UPS",
            LocalDate.now(),
            ParcelStatus.AVAILABLE,
            new PickupPoint("id", "Name", "Address", "Hours")
        );

        assertEquals("UPS", parcel.carrier());
    }
}