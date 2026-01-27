package com.parcelflow.infrastructure.bootstrap;

import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.model.ParcelStatus;
import com.parcelflow.domain.model.PickupPoint;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer {

    private final ParcelRepositoryPort repository;

    public DataInitializer(ParcelRepositoryPort repository) {
        this.repository = repository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        if (repository.findAll().isEmpty()) {
            PickupPoint pp1 = new PickupPoint("pp-1", "Relais Colis", "12 rue de la Paix, Paris", "08:00-19:00");
            PickupPoint pp2 = new PickupPoint("pp-2", "Point Relay", "45 Avenue de la République, Lyon", "09:00-20:00");

            List<Parcel> sampleParcels = List.of(
            new Parcel(ParcelId.random(), "TRACK-URGENT-1", "DHL", LocalDate.now().plusDays(1), ParcelStatus.AVAILABLE, pp1),
            new Parcel(ParcelId.random(), "TRACK-SOON-1", "UPS", LocalDate.now().plusDays(3), ParcelStatus.AVAILABLE, pp1),
            new Parcel(ParcelId.random(), "TRACK-SAFE-1", "La Poste", LocalDate.now().plusDays(7), ParcelStatus.AVAILABLE, pp2),
            new Parcel(ParcelId.random(), "TRACK-DONE-1", "Chronopost", LocalDate.now().minusDays(2), ParcelStatus.PICKED_UP, pp2)
            );

            repository.saveAll(sampleParcels);
            System.out.println("🚀 [Bootstrap] Données de démonstration chargées avec succès !");
        }
    }
}
