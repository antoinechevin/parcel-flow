package com.parcelflow.infrastructure.extraction;

import com.parcelflow.domain.model.ProviderDefinition;
import com.parcelflow.domain.ports.ProviderRegistryPort;
import com.parcelflow.infrastructure.adapters.extraction.chronopost.ChronopostReroutingStrategy;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ProviderRegistry implements ProviderRegistryPort {

    private final List<ProviderDefinition> providers;

    public ProviderRegistry(ChronopostPickupExtractionAdapter chronopostAdapter,
                            ChronopostReroutingStrategy chronopostReroutingStrategy,
                            MondialRelayExtractionAdapter mondialRelayAdapter,
                            VintedGoExtractionAdapter vintedGoAdapter) {
        this.providers = List.of(
            new ProviderDefinition(
                "Chronopost Rerouting", 
                "from:(chronopost@network1.pickup.fr OR chronopost@network2.pickup.fr) \"n’a pas pu être livré dans votre point initial\"", 
                chronopostReroutingStrategy
            ),
            new ProviderDefinition(
                "Chronopost", 
                "from:(chronopost@network1.pickup.fr OR chronopost@network2.pickup.fr) -\"n’a pas pu être livré dans votre point initial\"", 
                chronopostAdapter
            ),
            new ProviderDefinition(
                "Mondial Relay", 
                "from:(noreply@mondialrelay.fr OR notifications@shipup.co) subject:\"disponible\"", 
                mondialRelayAdapter
            ),
            new ProviderDefinition(
                "Vinted Go", 
                "from:(noreply@vinted.com OR no-reply@vinted.com) subject:(récupère ton colis OR récupérer ton colis)", 
                vintedGoAdapter
            )
        );
    }

    @Override
    public List<ProviderDefinition> getAllProviders() {
        return providers;
    }
}