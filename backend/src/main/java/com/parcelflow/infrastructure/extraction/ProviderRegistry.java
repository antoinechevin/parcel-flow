package com.parcelflow.infrastructure.extraction;

import com.parcelflow.domain.model.ProviderDefinition;
import com.parcelflow.domain.ports.ProviderRegistryPort;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ProviderRegistry implements ProviderRegistryPort {

    private final List<ProviderDefinition> providers;

    public ProviderRegistry(ChronopostPickupExtractionAdapter chronopostAdapter,
                            MondialRelayExtractionAdapter mondialRelayAdapter,
                            VintedGoExtractionAdapter vintedGoAdapter) {
        this.providers = List.of(
            new ProviderDefinition(
                "Chronopost", 
                "from:chronopost@network1.pickup.fr", 
                chronopostAdapter
            ),
            new ProviderDefinition(
                "Mondial Relay", 
                "from:noreply@mondialrelay.fr subject:\"disponible\"", 
                mondialRelayAdapter
            ),
            new ProviderDefinition(
                "Vinted Go", 
                "from:(noreply@vinted.com | no-reply@vinted.com) subject:(récupère ton colis | récupérer ton colis)", 
                vintedGoAdapter
            )
        );
    }

    @Override
    public List<ProviderDefinition> getAllProviders() {
        return providers;
    }
}