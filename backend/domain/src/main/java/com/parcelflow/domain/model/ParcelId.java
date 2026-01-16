package com.parcelflow.domain.model;

import java.util.Objects;

public record ParcelId(String value) {
    public ParcelId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ID required");
        }
    }
}
