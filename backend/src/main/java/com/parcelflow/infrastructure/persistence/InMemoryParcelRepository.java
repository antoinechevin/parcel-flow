package com.parcelflow.infrastructure.persistence;

import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.model.ParcelStatus;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryParcelRepository implements ParcelRepositoryPort {

    private final List<Parcel> parcels = new ArrayList<>(List.of(
        new Parcel(ParcelId.random(), "SHOES-123", LocalDate.now().plusDays(1), ParcelStatus.AVAILABLE),
        new Parcel(ParcelId.random(), "BOOK-456", LocalDate.now().plusDays(2), ParcelStatus.AVAILABLE),
        new Parcel(ParcelId.random(), "HAT-789", LocalDate.now().minusDays(1), ParcelStatus.PICKED_UP)
    ));

    @Override
    public List<Parcel> findAll() {
        return List.copyOf(parcels);
    }

    @Override
    public void saveAll(List<Parcel> newParcels) {
        this.parcels.clear();
        this.parcels.addAll(newParcels);
    }
}