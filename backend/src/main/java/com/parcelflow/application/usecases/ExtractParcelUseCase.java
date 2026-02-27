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

                Optional<Parcel> existingParcelOpt = repositoryPort.findByTrackingNumber(cleanTrackingNumber);
                
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

                if (existingParcelOpt.isPresent()) {
                    Parcel existingParcel = existingParcelOpt.get();
                    log.info("Parcel with tracking number {} already exists. Updating metadata...", cleanTrackingNumber);
                    
                    // Specific for rerouting/update: reset status to AVAILABLE and update location
                    Parcel updatedParcel = new Parcel(
                        existingParcel.id(),
                        cleanTrackingNumber,
                        metadata.carrier() != null ? metadata.carrier() : existingParcel.carrier(),
                        metadata.expirationDate() != null ? metadata.expirationDate() : existingParcel.deadline(),
                        ParcelStatus.AVAILABLE, // Reset status as new info received
                        pickupPoint != null ? pickupPoint : existingParcel.pickupPoint(),
                        metadata.pickupCode() != null ? metadata.pickupCode() : existingParcel.pickupCode(),
                        metadata.qrCodeUrl() != null ? metadata.qrCodeUrl() : existingParcel.qrCodeUrl(),
                        metadata.barcodeType() != null ? metadata.barcodeType() : existingParcel.barcodeType()
                    );
                    
                    if (pickupPoint == null && metadata.carrier() != null && metadata.carrier().contains("Rerouté")) {
                        log.warn("Rerouting detected but no new location found for {}. Keeping old location: {}", 
                            cleanTrackingNumber, existingParcel.pickupPoint().name());
                    }
                    
                    repositoryPort.save(updatedParcel);
                    log.info("Parcel updated successfully.");
                    return;
                }

                log.info("Metadata extracted: {}. Saving new parcel...", cleanTrackingNumber);

                Parcel parcel = new Parcel(
                    ParcelId.random(),
                    cleanTrackingNumber,
                    metadata.carrier(),
                    metadata.expirationDate(),
                    ParcelStatus.AVAILABLE,
                    pickupPoint,
                    metadata.pickupCode(),
                    metadata.qrCodeUrl(),
                    metadata.barcodeType()
                );
                
                repositoryPort.save(parcel);
                log.info("Parcel saved successfully.");
            },
            () -> log.warn("No metadata extracted from email content.")
        );

        return metadataOpt;
    }
}