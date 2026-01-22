package com.parcelflow.domain;

import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.model.ParcelStatus;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ParcelTest {

    @Test
    void shouldCreateParcelWithValidData() {
        ParcelId id = ParcelId.random();
        String trackingNumber = "123456789";
        LocalDate deadline = LocalDate.now().plusDays(3);
        ParcelStatus status = ParcelStatus.AVAILABLE;

        Parcel parcel = new Parcel(id, trackingNumber, deadline, status);

        assertNotNull(parcel);
        assertEquals(id, parcel.id());
        assertEquals(trackingNumber, parcel.trackingNumber());
        assertEquals(deadline, parcel.deadline());
        assertEquals(status, parcel.status());
    }

    @Test
    void statusShouldHaveExpectedValues() {
        assertEquals(2, ParcelStatus.values().length);
        assertNotNull(ParcelStatus.valueOf("AVAILABLE"));
        assertNotNull(ParcelStatus.valueOf("PICKED_UP"));
    }
}