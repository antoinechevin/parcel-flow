package com.parcelflow.domain.ports;

import com.parcelflow.domain.model.ProviderDefinition;
import java.util.List;

public interface ProviderRegistryPort {
    List<ProviderDefinition> getAllProviders();
}
