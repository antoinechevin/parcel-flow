package com.parcelflow.domain.model;

import com.parcelflow.domain.ports.ParcelExtractionPort;

/**
 * Definition of a mail provider for parcel extraction.
 */
public record ProviderDefinition(
    String name,
    String query,
    ParcelExtractionPort adapter
) {}
