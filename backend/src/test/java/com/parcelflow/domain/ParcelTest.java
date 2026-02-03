package com.parcelflow.domain;

import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.BarcodeType;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.model.ParcelStatus;
import com.parcelflow.domain.model.PickupPoint;
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
        PickupPoint pickupPoint = new PickupPoint("pp-1", "Relais", "Address", "08:00-19:00");

        Parcel parcel = new Parcel(id, trackingNumber, "UPS", deadline, status, pickupPoint, "1234", "http://qr.url", BarcodeType.QR_CODE);

        assertNotNull(parcel);
        assertEquals(id, parcel.id());
        assertEquals(trackingNumber, parcel.trackingNumber());
        assertEquals(deadline, parcel.deadline());
        assertEquals(status, parcel.status());
        assertEquals(pickupPoint, parcel.pickupPoint());
        assertEquals(BarcodeType.QR_CODE, parcel.barcodeType());
    }

    @Test
    void statusShouldHaveExpectedValues() {
        assertEquals(4, ParcelStatus.values().length);
        assertNotNull(ParcelStatus.valueOf("AVAILABLE"));
        assertNotNull(ParcelStatus.valueOf("PICKED_UP"));
        assertNotNull(ParcelStatus.valueOf("EXPIRED"));
        assertNotNull(ParcelStatus.valueOf("ARCHIVED"));
    }
}
