package com.parcelflow.domain.ports;

import com.parcelflow.domain.model.Parcel;
import java.util.List;

public interface ParcelRepositoryPort {

    List<Parcel> findAll();

    java.util.Optional<Parcel> findByTrackingNumber(String trackingNumber);

    void save(Parcel parcel);

    void saveAll(java.util.List<Parcel> parcels);

    void deleteAll();

}
