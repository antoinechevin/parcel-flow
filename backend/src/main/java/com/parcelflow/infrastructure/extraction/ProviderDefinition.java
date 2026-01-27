package com.parcelflow.infrastructure.extraction;

import com.parcelflow.domain.ports.ParcelExtractionPort;

public record ProviderDefinition(
    String name,
    String query,
    ParcelExtractionPort adapter
) {}
