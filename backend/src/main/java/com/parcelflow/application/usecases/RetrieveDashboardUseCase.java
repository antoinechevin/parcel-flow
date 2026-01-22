package com.parcelflow.application.usecases;

import com.parcelflow.domain.model.LocationGroup;
import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.ports.ParcelRepositoryPort;

import java.util.List;
import java.util.stream.Collectors;

public class RetrieveDashboardUseCase {

    private final ParcelRepositoryPort repository;

    public RetrieveDashboardUseCase(ParcelRepositoryPort repository) {
        this.repository = repository;
    }

    public List<LocationGroup> retrieve() {
        return repository.findAll().stream()
            .collect(Collectors.groupingBy(Parcel::pickupPoint))
            .entrySet().stream()
            .map(entry -> new LocationGroup(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }
}