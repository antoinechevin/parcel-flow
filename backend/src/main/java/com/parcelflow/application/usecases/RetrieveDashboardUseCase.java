package com.parcelflow.application.usecases;

import com.parcelflow.domain.model.LocationGroup;
import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelStatus;
import com.parcelflow.domain.model.UrgencyLevel;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import com.parcelflow.domain.service.UrgencyCalculator;

import java.time.LocalDate;
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

            LocalDate today = LocalDate.now(urgencyCalculator.getClock());

            

            return repository.findAll().stream()

                .map(p -> {

                    ParcelStatus effective = p.effectiveStatus(today);

                    if (effective != p.status()) {

                        return new Parcel(p.id(), p.trackingNumber(), p.carrier(), p.deadline(), effective, p.pickupPoint(), p.pickupCode(), p.qrCodeUrl());

                    }

                    return p;

                })

                .collect(Collectors.groupingBy(Parcel::pickupPoint))

                .entrySet().stream()

                .map(entry -> {

                    List<Parcel> parcels = entry.getValue();

                    UrgencyCalculator.Result result = urgencyCalculator.calculate(parcels, today);

                    

                    List<Parcel> sortedParcels = parcels.stream()

                        .sorted(Comparator.comparing(this::getParcelPriority).thenComparing(Parcel::deadline))

                        .collect(Collectors.toList());

    

                    return new LocationGroup(entry.getKey(), sortedParcels, result.level(), result.daysUntil());

                })

                .sorted(Comparator.comparing(this::getGroupPriority).thenComparing(g -> g.urgency()).thenComparing(g -> g.pickupPoint().name()))

                .collect(Collectors.toList());

        }

    

        private int getParcelPriority(Parcel p) {

            return switch (p.status()) {

                case AVAILABLE -> 0;

                case EXPIRED -> 1;

                case PICKED_UP -> 2;

            };

        }

    

        private int getGroupPriority(LocationGroup g) {

            boolean hasAvailable = g.parcels().stream().anyMatch(p -> p.status() == ParcelStatus.AVAILABLE);

            return hasAvailable ? 0 : 1;

        }

    }

    