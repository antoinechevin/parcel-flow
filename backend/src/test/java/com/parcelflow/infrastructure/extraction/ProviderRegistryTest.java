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

    @Test
    void should_contain_all_providers_with_correct_queries() {
        List<ProviderDefinition> providers = registry.getAllProviders();
        
        assertEquals(3, providers.size());

        ProviderDefinition chronopost = findProvider(providers, "Chronopost");
        assertEquals("from:chronopost@network1.pickup.fr", chronopost.query());
        assertSame(chronopostAdapter, chronopost.adapter());

        ProviderDefinition mondialRelay = findProvider(providers, "Mondial Relay");
        assertEquals("from:noreply@mondialrelay.fr subject:\"disponible\"", mondialRelay.query());
        assertSame(mondialRelayAdapter, mondialRelay.adapter());

        ProviderDefinition vintedGo = findProvider(providers, "Vinted Go");
        assertEquals("from:(noreply@vinted.com | no-reply@vinted.com) subject:(récupère ton colis | récupérer ton colis)", vintedGo.query());
        assertSame(vintedGoAdapter, vintedGo.adapter());
    }

    private ProviderDefinition findProvider(List<ProviderDefinition> providers, String name) {
        return providers.stream()
                .filter(p -> p.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Provider not found: " + name));
    }
}
