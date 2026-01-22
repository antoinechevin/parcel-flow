package com.parcelflow.application;

import com.parcelflow.domain.Parcel;
import com.parcelflow.domain.ParcelRepositoryPort;

import java.util.List;

public class RetrieveDashboardUseCase {

    private final ParcelRepositoryPort repository;

    public RetrieveDashboardUseCase(ParcelRepositoryPort repository) {
        this.repository = repository;
    }

    public List<Parcel> retrieve() {
        return repository.findAll();
    }
}
