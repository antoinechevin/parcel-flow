package com.parcelflow.infrastructure.adapter.web;

import com.parcelflow.domain.model.Parcel;

public record ParcelResponse(String id, String label, String status) {
    public static ParcelResponse from(Parcel parcel) {
        return new ParcelResponse(
                parcel.getId().value(),
                parcel.getLabel(),
                parcel.getStatus().name()
        );
    }
}
