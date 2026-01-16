package com.parcelflow.domain.model;

import java.util.Objects;

public class Parcel {
    private final ParcelId id;
    private final String label;
    private final ParcelStatus status;

    private Parcel(ParcelId id, String label, ParcelStatus status) {
        this.id = id;
        this.label = label;
        this.status = status;
    }

    public static Parcel create(ParcelId id, String label) {
        if (id == null) {
            throw new IllegalArgumentException("ID required");
        }
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("Label required");
        }
        return new Parcel(id, label, ParcelStatus.CREATED);
    }

    public ParcelId getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public ParcelStatus getStatus() {
        return status;
    }
}
