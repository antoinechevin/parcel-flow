package com.parcelflow.application.usecases;

import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.ports.ParcelRepositoryPort;

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