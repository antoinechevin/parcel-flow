package com.parcelflow.application.usecases;

import com.parcelflow.domain.model.LocationGroup;
import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.UrgencyLevel;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import com.parcelflow.domain.service.UrgencyCalculator;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RetrieveDashboardUseCase {

    private final ParcelRepositoryPort repository;
    private final UrgencyCalculator urgencyCalculator;

    public RetrieveDashboardUseCase(ParcelRepositoryPort repository, UrgencyCalculator urgencyCalculator) {
        this.repository = repository;
        this.urgencyCalculator = urgencyCalculator;
    }

    public List<LocationGroup> retrieve() {
        return repository.findAll().stream()
            .collect(Collectors.groupingBy(Parcel::pickupPoint))
            .entrySet().stream()
            .map(entry -> {
                List<Parcel> parcels = entry.getValue();
                UrgencyCalculator.Result result = urgencyCalculator.calculate(parcels);
                return new LocationGroup(entry.getKey(), parcels, result.level(), result.daysUntil());
            })
            .sorted(Comparator.comparing((LocationGroup g) -> g.urgency()).thenComparing(g -> g.pickupPoint().name()))
            .collect(Collectors.toList());
    }
}