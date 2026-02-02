package com.parcelflow.domain;

import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.BarcodeType;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.model.ParcelStatus;
import com.parcelflow.domain.model.PickupPoint;
import com.parcelflow.domain.model.LocationGroup;
import com.parcelflow.domain.model.UrgencyLevel;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DomainModelingTest {

    @Test
    void shouldCreatePickupPoint() {
        PickupPoint pickupPoint = new PickupPoint("pp-1", "Relais Colis", "12 rue de la Paix", "08:00-19:00");
        assertEquals("pp-1", pickupPoint.id());
        assertEquals("Relais Colis", pickupPoint.name());
        assertEquals("12 rue de la Paix", pickupPoint.rawAddress());
    }

    @Test
    void shouldIncludePickupPointInParcel() {
        PickupPoint pickupPoint = new PickupPoint("pp-1", "Relais Colis", "12 rue de la Paix", "08:00-19:00");
        Parcel parcel = new Parcel(
            ParcelId.random(),
            "TRACK-1",
            "UPS",
            LocalDate.now().plusDays(2),
            ParcelStatus.AVAILABLE,
            pickupPoint,
            null,
            null
        , BarcodeType.QR_CODE);
        assertEquals(pickupPoint, parcel.pickupPoint());
    }

    @Test
    void shouldCreateLocationGroup() {
        PickupPoint pickupPoint = new PickupPoint("pp-1", "Relais Colis", "12 rue de la Paix", "08:00-19:00");
        Parcel parcel = new Parcel(
            ParcelId.random(),
            "TRACK-1",
            "UPS",
            LocalDate.now().plusDays(2),
            ParcelStatus.AVAILABLE,
            pickupPoint,
            null,
            null
        , BarcodeType.QR_CODE);
        
        LocationGroup group = new LocationGroup(pickupPoint, List.of(parcel), UrgencyLevel.LOW, 5);
        
        assertEquals(pickupPoint, group.pickupPoint());
        assertEquals(1, group.parcels().size());
        assertEquals(parcel, group.parcels().get(0));
    }
}
