package com.parcelflow.domain.model;

import java.time.LocalDate;

public record Parcel(
    ParcelId id,
    String trackingNumber,
    String carrier,
    LocalDate deadline,
    ParcelStatus status,
    PickupPoint pickupPoint,
    String pickupCode,
    String qrCodeUrl,
    BarcodeType barcodeType
) {
    public ParcelStatus effectiveStatus(LocalDate today) {
        if (status == ParcelStatus.AVAILABLE && deadline != null && deadline.isBefore(today)) {
            return ParcelStatus.EXPIRED;
        }
        return status;
    }
}