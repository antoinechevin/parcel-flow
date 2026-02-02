package com.parcelflow.application;

import com.parcelflow.application.usecases.RetrieveDashboardUseCase;
import com.parcelflow.domain.model.*;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import com.parcelflow.domain.service.UrgencyCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RetrieveDashboardUseCaseTest {

    private RetrieveDashboardUseCase useCase;
    private ParcelRepositoryPort repository;
    private UrgencyCalculator urgencyCalculator;
    private final Clock clock = Clock.fixed(Instant.parse("2026-02-02T10:00:00Z"), ZoneId.of("UTC"));

    @BeforeEach
    void setUp() {
        repository = mock(ParcelRepositoryPort.class);
        urgencyCalculator = new UrgencyCalculator(clock);
        useCase = new RetrieveDashboardUseCase(repository, urgencyCalculator);
    }

    @Test
    void shouldRetrieveDashboardGroupedByLocation() {
        PickupPoint pp1 = new PickupPoint("pp-1", "Relais 1", "Addr 1", "08:00-19:00");
        PickupPoint pp2 = new PickupPoint("P2", "Relais 2", "Addr 2", "08-19");

        Parcel p1 = new Parcel(ParcelId.random(), "T1", "DHL", LocalDate.now(), ParcelStatus.AVAILABLE, pp1, null, null, BarcodeType.QR_CODE);
        Parcel p2 = new Parcel(ParcelId.random(), "T2", "UPS", LocalDate.now(), ParcelStatus.AVAILABLE, pp1, null, null, BarcodeType.QR_CODE);
        Parcel p3 = new Parcel(ParcelId.random(), "T3", "La Poste", LocalDate.now(), ParcelStatus.AVAILABLE, pp2, null, null, BarcodeType.QR_CODE);

        when(repository.findAll()).thenReturn(List.of(p1, p2, p3));

        List<LocationGroup> actualGroups = useCase.retrieve();

        assertEquals(2, actualGroups.size());
        assertEquals("Relais 1", actualGroups.get(0).pickupPoint().name());
        assertEquals(2, actualGroups.get(0).parcels().size());
    }
}
