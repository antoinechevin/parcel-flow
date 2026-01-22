package com.parcelflow.domain.model;

import java.util.List;

public record LocationGroup(
    PickupPoint pickupPoint,
    List<Parcel> parcels,
    UrgencyLevel urgency,
    Integer daysUntilExpiration
) {}
