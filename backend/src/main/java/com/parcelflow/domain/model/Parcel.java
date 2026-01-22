package com.parcelflow.domain.model;

import java.time.LocalDate;

public record Parcel(
    ParcelId id,
    String trackingNumber,
    LocalDate deadline,
    ParcelStatus status,
    PickupPoint pickupPoint
) {
    public boolean isUrgent() {
        return status != ParcelStatus.PICKED_UP 
            && deadline.isBefore(LocalDate.now().plusDays(2));
    }
}