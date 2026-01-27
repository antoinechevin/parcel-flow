package com.parcelflow.application.usecases;

import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.model.ParcelMetadata;
import com.parcelflow.domain.model.ParcelStatus;
import com.parcelflow.domain.model.PickupPoint;
import com.parcelflow.domain.ports.ParcelExtractionPort;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.Optional;

public class ExtractParcelUseCase {

    private static final Logger log = LoggerFactory.getLogger(ExtractParcelUseCase.class);
    private final ParcelExtractionPort extractionPort;
    private final ParcelRepositoryPort repositoryPort;

    public ExtractParcelUseCase(ParcelExtractionPort extractionPort, ParcelRepositoryPort repositoryPort) {
        this.extractionPort = extractionPort;
        this.repositoryPort = repositoryPort;
    }

    public void execute(String emailContent, ZonedDateTime receivedAt) {
        execute(emailContent, receivedAt, this.extractionPort);
    }

    public void execute(String emailContent, ZonedDateTime receivedAt, ParcelExtractionPort specificAdapter) {
        log.info("Executing parcel extraction use case with specific adapter...");
        
        Optional<ParcelMetadata> metadataOpt = specificAdapter.extract(emailContent, receivedAt);
        
        metadataOpt.ifPresentOrElse(
            metadata -> {
                if (repositoryPort.findByTrackingNumber(metadata.trackingCode()).isPresent()) {
                    log.info("Parcel with tracking number {} already exists. Skipping.", metadata.trackingCode());
                    return;
                }

                log.info("Metadata extracted: {}. Saving parcel...", metadata.trackingCode());
                
                PickupPoint pickupPoint = null;
                if (metadata.pickupLocation() != null && !metadata.pickupLocation().isBlank()) {
                    pickupPoint = new PickupPoint(
                        "extracted-" + metadata.trackingCode(),
                        metadata.pickupLocation(),
                        metadata.pickupLocation(),
                        null // Leave opening hours null instead of "Unknown"
                    );
                }

                Parcel parcel = new Parcel(
                    ParcelId.random(),
                    metadata.trackingCode(),
                    metadata.carrier(),
                    metadata.expirationDate(),
                    ParcelStatus.AVAILABLE,
                    pickupPoint
                );
                
                repositoryPort.save(parcel);
                log.info("Parcel saved successfully.");
            },
            () -> log.warn("No metadata extracted from email content.")
        );
    }
}
