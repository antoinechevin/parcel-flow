package com.parcelflow.infrastructure.persistence;

import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.model.ParcelStatus;
import com.parcelflow.domain.model.PickupPoint;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository

public class InMemoryParcelRepository implements ParcelRepositoryPort {



    private final List<Parcel> parcels = new ArrayList<>();



    @Override

    public List<Parcel> findAll() {

        return List.copyOf(parcels);

    }



    @Override
    public synchronized java.util.Optional<Parcel> findByTrackingNumber(String trackingNumber) {
        return parcels.stream()
            .filter(p -> p.trackingNumber().equals(trackingNumber))
            .findFirst();
    }

    @Override
    public synchronized void save(Parcel parcel) {
        parcels.removeIf(p -> p.trackingNumber().equals(parcel.trackingNumber()));
        this.parcels.add(parcel);
    }

    @Override
    public synchronized void saveAll(List<Parcel> newParcels) {
        newParcels.forEach(this::save);
    }



    @Override

    public void deleteAll() {

        this.parcels.clear();

    }

}
