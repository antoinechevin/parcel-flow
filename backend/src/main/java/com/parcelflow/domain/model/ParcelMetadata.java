package com.parcelflow.domain.model;

import java.time.LocalDate;

public record ParcelMetadata(
    String trackingCode,
    String pickupCode,
    String carrier,
    LocalDate expirationDate,
    String pickupLocation
) {
}
