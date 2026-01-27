package com.parcelflow.application;

import com.parcelflow.application.usecases.RetrieveDashboardUseCase;
import com.parcelflow.domain.model.LocationGroup;
import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.model.ParcelStatus;
import com.parcelflow.domain.model.PickupPoint;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import com.parcelflow.domain.service.UrgencyCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetrieveDashboardUseCaseTest {

    @Mock
    private ParcelRepositoryPort repository;

    @Test
    void shouldReturnGroupedParcelsFromRepository() {
        RetrieveDashboardUseCase useCase = new RetrieveDashboardUseCase(repository, new UrgencyCalculator(java.time.Clock.systemDefaultZone()));
        PickupPoint pp1 = new PickupPoint("pp-1", "Relais 1", "Addr 1", "08:00-19:00");
        PickupPoint pp2 = new PickupPoint("P2", "Relais 2", "Addr 2", "08-19");

        Parcel p1 = new Parcel(ParcelId.random(), "T1", "DHL", LocalDate.now(), ParcelStatus.AVAILABLE, pp1);
        Parcel p2 = new Parcel(ParcelId.random(), "T2", "UPS", LocalDate.now(), ParcelStatus.AVAILABLE, pp1);
        Parcel p3 = new Parcel(ParcelId.random(), "T3", "La Poste", LocalDate.now(), ParcelStatus.AVAILABLE, pp2);

        when(repository.findAll()).thenReturn(List.of(p1, p2, p3));

        List<LocationGroup> actualGroups = useCase.retrieve();

        assertEquals(2, actualGroups.size());
        
        LocationGroup g1 = actualGroups.stream().filter(g -> g.pickupPoint().equals(pp1)).findFirst().orElseThrow();
        assertEquals(2, g1.parcels().size());
        
        LocationGroup g2 = actualGroups.stream().filter(g -> g.pickupPoint().equals(pp2)).findFirst().orElseThrow();
        assertEquals(1, g2.parcels().size());
    }
}