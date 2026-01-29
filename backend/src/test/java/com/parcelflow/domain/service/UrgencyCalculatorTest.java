package com.parcelflow.domain.service;

import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.model.ParcelStatus;
import com.parcelflow.domain.model.PickupPoint;
import com.parcelflow.domain.model.UrgencyLevel;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UrgencyCalculatorTest {

    private final Clock fixedClock = Clock.fixed(Instant.parse("2026-01-22T10:00:00Z"), ZoneId.of("UTC"));
    private final UrgencyCalculator calculator = new UrgencyCalculator(fixedClock);
    private final PickupPoint pp = new PickupPoint("pp-1", "Relais", "Address", "08:00-19:00");

    @Test
    void should_return_HIGH_when_parcel_expires_tomorrow() {
        LocalDate today = LocalDate.now(fixedClock);
        Parcel p = new Parcel(ParcelId.random(), "T1", "DHL", today.plusDays(1), ParcelStatus.AVAILABLE, pp);
        UrgencyCalculator.Result result = calculator.calculate(List.of(p), today);
        assertEquals(UrgencyLevel.HIGH, result.level());
        assertEquals(1, result.daysUntil());
    }

    @Test
    void should_return_MEDIUM_when_parcel_expires_in_3_days() {
        LocalDate today = LocalDate.now(fixedClock);
        Parcel p = new Parcel(ParcelId.random(), "T1", "DHL", today.plusDays(3), ParcelStatus.AVAILABLE, pp);
        UrgencyCalculator.Result result = calculator.calculate(List.of(p), today);
        assertEquals(UrgencyLevel.MEDIUM, result.level());
        assertEquals(3, result.daysUntil());
    }

    @Test
    void should_return_LOW_when_parcel_expires_in_5_days() {
        LocalDate today = LocalDate.now(fixedClock);
        Parcel p = new Parcel(ParcelId.random(), "T1", "DHL", today.plusDays(5), ParcelStatus.AVAILABLE, pp);
        UrgencyCalculator.Result result = calculator.calculate(List.of(p), today);
        assertEquals(UrgencyLevel.LOW, result.level());
        assertEquals(5, result.daysUntil());
    }

    @Test
    void should_ignore_picked_up_parcels() {
        LocalDate today = LocalDate.now(fixedClock);
        Parcel p1 = new Parcel(ParcelId.random(), "T1", "DHL", today.plusDays(1), ParcelStatus.PICKED_UP, pp);
        Parcel p2 = new Parcel(ParcelId.random(), "T2", "DHL", today.plusDays(5), ParcelStatus.AVAILABLE, pp);
        UrgencyCalculator.Result result = calculator.calculate(List.of(p1, p2), today);
        assertEquals(UrgencyLevel.LOW, result.level());
        assertEquals(5, result.daysUntil());
    }

    @Test
    void should_ignore_expired_parcels() {
        LocalDate today = LocalDate.now(fixedClock);
        Parcel p1 = new Parcel(ParcelId.random(), "T1", "DHL", today.minusDays(1), ParcelStatus.EXPIRED, pp);
        Parcel p2 = new Parcel(ParcelId.random(), "T2", "DHL", today.plusDays(5), ParcelStatus.AVAILABLE, pp);
        UrgencyCalculator.Result result = calculator.calculate(List.of(p1, p2), today);
        assertEquals(UrgencyLevel.LOW, result.level());
        assertEquals(5, result.daysUntil());
    }
}
