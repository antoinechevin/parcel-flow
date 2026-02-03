package com.parcelflow.application.usecases;

import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelStatus;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

public class ArchiveParcelUseCase {

    private final ParcelRepositoryPort repository;

    public ArchiveParcelUseCase(ParcelRepositoryPort repository) {
        this.repository = repository;
    }

    public void archive(String trackingNumber) {
        Parcel parcel = repository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new IllegalArgumentException("Parcel not found with tracking number: " + trackingNumber));
        
        Parcel archivedParcel = new Parcel(
            parcel.id(),
            parcel.trackingNumber(),
            parcel.carrier(),
            parcel.deadline(),
            ParcelStatus.ARCHIVED,
            parcel.pickupPoint(),
            parcel.pickupCode(),
            parcel.qrCodeUrl(),
            parcel.barcodeType()
        );
        repository.save(archivedParcel);
    }
}
