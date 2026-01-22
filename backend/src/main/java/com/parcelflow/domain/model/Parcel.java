package com.parcelflow.domain.model;

import java.time.LocalDate;

public record Parcel(

    ParcelId id,

    String trackingNumber,

    LocalDate deadline,

    ParcelStatus status,

    PickupPoint pickupPoint

) {

}
