package com.parcelflow.domain;

import java.time.LocalDate;
import java.util.UUID;

public record Parcel(
    UUID id,
    String trackingNumber,
    LocalDate deadline,
    ParcelStatus status
) {}
