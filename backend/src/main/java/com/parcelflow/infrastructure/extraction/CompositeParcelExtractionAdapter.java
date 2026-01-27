package com.parcelflow.infrastructure.extraction;

import com.parcelflow.domain.model.ParcelMetadata;
import com.parcelflow.domain.ports.ParcelExtractionPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Primary
public class CompositeParcelExtractionAdapter implements ParcelExtractionPort {

    private final List<ParcelExtractionPort> adapters;

    public CompositeParcelExtractionAdapter(List<ParcelExtractionPort> allAdapters) {
        // Exclude self from the list of delegates
        this.adapters = allAdapters.stream()
                .filter(adapter -> !(adapter instanceof CompositeParcelExtractionAdapter))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ParcelMetadata> extract(String emailContent, ZonedDateTime receivedAt) {
        for (ParcelExtractionPort adapter : adapters) {
            try {
                Optional<ParcelMetadata> result = adapter.extract(emailContent, receivedAt);
                if (result.isPresent()) {
                    return result;
                }
            } catch (Exception e) {
                // Continue to next adapter
            }
        }
        return Optional.empty();
    }
}
