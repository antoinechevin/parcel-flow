package com.parcelflow.domain.ports;

import com.parcelflow.domain.model.ParcelMetadata;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface ParcelExtractionPort {
    /**
     * Extracts parcel metadata from a raw email content.
     * @param emailContent the content of the email
     * @param receivedAt the date the email was received
     * @return extracted metadata or empty if extraction failed or no parcel found
     */
    Optional<ParcelMetadata> extract(String emailContent, ZonedDateTime receivedAt);
}
