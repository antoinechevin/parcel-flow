package com.parcelflow.domain.model;

public enum UrgencyLevel {
    HIGH,    // Red: expires in 0-1 days
    MEDIUM,  // Orange: expires in 2-3 days
    LOW      // Blue: expires in 4+ days
}
