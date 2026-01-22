package com.parcelflow.domain.model;

public record PickupPoint(
    String id,
    String name,
    String rawAddress,
    String openingHours
) {}
