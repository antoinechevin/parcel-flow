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
import com.parcelflow.domain.model.BarcodeType;

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
                if (existingParcelOpt.isPresent()) {
                    log.info("Parcel with tracking number {} already exists. Updating it...", cleanTrackingNumber);
                } else {
                    log.info("Metadata extracted: {}. Creating new parcel...", cleanTrackingNumber);
                }
                
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

                ParcelId parcelId = existingParcelOpt.map(Parcel::id).orElseGet(ParcelId::random);
                ParcelStatus status = existingParcelOpt.map(Parcel::status).orElse(ParcelStatus.AVAILABLE);

                // Intelligent merge: keep old data if new extraction is missing it
                java.time.LocalDate finalExpirationDate = metadata.expirationDate() != null ?
                        metadata.expirationDate() :
                        existingParcelOpt.map(Parcel::deadline).orElse(null);

                String finalPickupCode = metadata.pickupCode() != null ?
                        metadata.pickupCode() :
                        existingParcelOpt.map(Parcel::pickupCode).orElse(null);

                String finalQrCodeUrl = metadata.qrCodeUrl() != null ?
                        metadata.qrCodeUrl() :
                        existingParcelOpt.map(Parcel::qrCodeUrl).orElse(null);

                BarcodeType finalBarcodeType = metadata.barcodeType() != null && metadata.barcodeType() != BarcodeType.NONE ?
                        metadata.barcodeType() :
                        existingParcelOpt.map(Parcel::barcodeType).orElse(BarcodeType.NONE);

                PickupPoint finalPickupPoint = pickupPoint != null ? pickupPoint :
                        existingParcelOpt.map(Parcel::pickupPoint).orElse(null);

                Parcel parcel = new Parcel(
                    parcelId,
                    cleanTrackingNumber,
                    metadata.carrier(),
                    finalExpirationDate,
                    status,
                    finalPickupPoint,
                    finalPickupCode,
                    finalQrCodeUrl,
                    finalBarcodeType
                );
                
                repositoryPort.save(parcel);
                log.info("Parcel saved/updated successfully.");
            },
            () -> log.warn("No metadata extracted from email content.")
        );

        return metadataOpt;
    }
}