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

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

public class ExtractParcelUseCase {

    private static final Logger log = LoggerFactory.getLogger(ExtractParcelUseCase.class);
    private final ParcelExtractionPort extractionPort;
    private final ParcelRepositoryPort repositoryPort;

    public ExtractParcelUseCase(ParcelExtractionPort extractionPort, ParcelRepositoryPort repositoryPort) {
        this.extractionPort = extractionPort;
        this.repositoryPort = repositoryPort;
    }

    public Optional<ParcelMetadata> execute(String emailContent, ZonedDateTime receivedAt) {
        return execute(emailContent, receivedAt, this.extractionPort);
    }

    public Optional<ParcelMetadata> execute(String emailContent, ZonedDateTime receivedAt, ParcelExtractionPort specificAdapter) {
        log.info("Executing parcel extraction use case with specific adapter...");
        
        Optional<ParcelMetadata> metadataOpt = specificAdapter.extract(emailContent, receivedAt);
        
        metadataOpt.ifPresentOrElse(
            metadata -> {
                String cleanTrackingNumber = metadata.trackingCode() != null ? metadata.trackingCode().trim() : null;
                
                if (cleanTrackingNumber == null || cleanTrackingNumber.isBlank()) {
                    log.warn("Extracted tracking number is empty. Skipping.");
                    return;
                }

                if (repositoryPort.findByTrackingNumber(cleanTrackingNumber).isPresent()) {
                    log.info("Parcel with tracking number {} already exists. Skipping.", cleanTrackingNumber);
                    return;
                }

                log.info("Metadata extracted: {}. Saving parcel...", cleanTrackingNumber);
                
                PickupPoint pickupPoint = null;
                if (metadata.pickupLocation() != null && !metadata.pickupLocation().isBlank()) {
                    String locationName = metadata.pickupLocation().trim();
                    pickupPoint = new PickupPoint(
                        "loc-" + UUID.nameUUIDFromBytes(locationName.toLowerCase().getBytes(StandardCharsets.UTF_8)).toString(),
                        locationName,
                        metadata.pickupLocation(),
                        null // Leave opening hours null instead of "Unknown"
                    );
                }

                Parcel parcel = new Parcel(
                    ParcelId.random(),
                    cleanTrackingNumber,
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

        return metadataOpt;
    }
}