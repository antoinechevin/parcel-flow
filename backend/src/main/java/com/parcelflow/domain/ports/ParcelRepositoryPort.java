package com.parcelflow.domain.ports;

import com.parcelflow.domain.model.Parcel;
import java.util.List;

public interface ParcelRepositoryPort {
    List<Parcel> findAll();
    void saveAll(List<Parcel> parcels);
}