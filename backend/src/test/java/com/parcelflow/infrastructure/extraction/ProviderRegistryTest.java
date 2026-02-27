package com.parcelflow.infrastructure.extraction;

import com.parcelflow.domain.model.ProviderDefinition;
import com.parcelflow.domain.ports.ParcelExtractionPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ProviderRegistryTest {

    @Autowired
    private ProviderRegistry registry;

    @Autowired
    private ChronopostPickupExtractionAdapter chronopostAdapter;

    @Autowired
    private MondialRelayExtractionAdapter mondialRelayAdapter;

    @Autowired
    private VintedGoExtractionAdapter vintedGoAdapter;

    @Autowired
    private com.parcelflow.infrastructure.adapters.extraction.chronopost.ChronopostReroutingStrategy chronopostReroutingStrategy;

    @Test
    void should_contain_all_providers_with_correct_queries() {
        List<ProviderDefinition> providers = registry.getAllProviders();
        
        assertEquals(4, providers.size());

        ProviderDefinition chronopost = findProvider(providers, "Chronopost");
        assertEquals("from:(chronopost@network1.pickup.fr OR chronopost@network2.pickup.fr) -\"n’a pas pu être livré dans votre point initial\"", chronopost.query());
        assertSame(chronopostAdapter, chronopost.adapter());

        ProviderDefinition rerouting = findProvider(providers, "Chronopost Rerouting");
        assertEquals("from:(chronopost@network1.pickup.fr OR chronopost@network2.pickup.fr) \"n’a pas pu être livré dans votre point initial\"", rerouting.query());
        assertSame(chronopostReroutingStrategy, rerouting.adapter());

        ProviderDefinition mondialRelay = findProvider(providers, "Mondial Relay");
        assertEquals("from:(noreply@mondialrelay.fr OR notifications@shipup.co) subject:\"disponible\"", mondialRelay.query());
        assertSame(mondialRelayAdapter, mondialRelay.adapter());

        ProviderDefinition vintedGo = findProvider(providers, "Vinted Go");
        assertEquals("from:(noreply@vinted.com OR no-reply@vinted.com) subject:(récupère ton colis OR récupérer ton colis)", vintedGo.query());
        assertSame(vintedGoAdapter, vintedGo.adapter());
    }

    private ProviderDefinition findProvider(List<ProviderDefinition> providers, String name) {
        return providers.stream()
                .filter(p -> p.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Provider not found: " + name));
    }
}
