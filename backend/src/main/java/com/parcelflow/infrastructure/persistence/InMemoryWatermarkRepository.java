package com.parcelflow.infrastructure.persistence;

import com.parcelflow.domain.ports.WatermarkRepositoryPort;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryWatermarkRepository implements WatermarkRepositoryPort {

    private final Map<String, ZonedDateTime> watermarks = new ConcurrentHashMap<>();

    @Override
    public Optional<ZonedDateTime> getWatermark(String providerName) {
        return Optional.ofNullable(watermarks.get(providerName));
    }

    @Override
    public void saveWatermark(String providerName, ZonedDateTime watermark) {
        watermarks.put(providerName, watermark);
    }
}
