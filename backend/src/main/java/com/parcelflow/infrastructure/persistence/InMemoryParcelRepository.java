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

    public void saveAll(List<Parcel> newParcels) {

        this.parcels.addAll(newParcels);

    }



    @Override

    public void deleteAll() {

        this.parcels.clear();

    }

}
