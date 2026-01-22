package com.parcelflow.domain;

import java.util.List;

public interface ParcelRepositoryPort {
    List<Parcel> findAll();
}
