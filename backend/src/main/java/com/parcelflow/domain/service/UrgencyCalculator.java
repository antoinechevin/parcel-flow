package com.parcelflow.domain.service;

import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelStatus;
import com.parcelflow.domain.model.UrgencyLevel;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class UrgencyCalculator {

    private final Clock clock;

    public UrgencyCalculator(Clock clock) {
        this.clock = clock;
    }

    public Clock getClock() {
        return clock;
    }

    public record Result(UrgencyLevel level, Integer daysUntil) {}

    public Result calculate(List<Parcel> parcels, LocalDate today) {
        return parcels.stream()
            .filter(p -> p.status() == ParcelStatus.AVAILABLE)
            .map(Parcel::deadline)
            .min(LocalDate::compareTo)
            .map(deadline -> {
                long daysUntil = ChronoUnit.DAYS.between(today, deadline);
                return new Result(mapToLevel(daysUntil), (int) daysUntil);
            })
            .orElse(new Result(UrgencyLevel.LOW, null));
    }

    private UrgencyLevel mapToLevel(long daysUntil) {
        if (daysUntil <= 1) return UrgencyLevel.HIGH;
        if (daysUntil <= 3) return UrgencyLevel.MEDIUM;
        return UrgencyLevel.LOW;
    }
}
