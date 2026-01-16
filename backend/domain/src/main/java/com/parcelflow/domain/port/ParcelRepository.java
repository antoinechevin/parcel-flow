package com.parcelflow.domain.port;

import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;

import java.util.List;
import java.util.Optional;

public interface ParcelRepository {
    void save(Parcel parcel);
    Optional<Parcel> findById(ParcelId id);
    List<Parcel> findAll();
}
