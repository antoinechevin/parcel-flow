package com.parcelflow.domain;

import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.model.ParcelStatus;
import com.parcelflow.domain.model.PickupPoint;
import com.parcelflow.domain.model.LocationGroup;
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
            "TRACK123",
            LocalDate.now().plusDays(5),
            ParcelStatus.AVAILABLE,
            pickupPoint
        );
        assertEquals(pickupPoint, parcel.pickupPoint());
    }

    @Test
    void shouldCreateLocationGroup() {
        PickupPoint pickupPoint = new PickupPoint("pp-1", "Relais Colis", "12 rue de la Paix", "08:00-19:00");
        Parcel parcel = new Parcel(
            ParcelId.random(),
            "TRACK123",
            LocalDate.now().plusDays(5),
            ParcelStatus.AVAILABLE,
            pickupPoint
        );
        
        LocationGroup group = new LocationGroup(pickupPoint, List.of(parcel));
        
        assertEquals(pickupPoint, group.pickupPoint());
        assertEquals(1, group.parcels().size());
        assertEquals(parcel, group.parcels().get(0));
    }
}
