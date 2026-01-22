package com.parcelflow.domain.model;

import java.util.Objects;
import java.util.UUID;

public record ParcelId(UUID value) {
    public ParcelId {
        Objects.requireNonNull(value);
    }
    public static ParcelId random() {
        return new ParcelId(UUID.randomUUID());
    }
    public static ParcelId fromString(String uuid) {
        return new ParcelId(UUID.fromString(uuid));
    }
}
