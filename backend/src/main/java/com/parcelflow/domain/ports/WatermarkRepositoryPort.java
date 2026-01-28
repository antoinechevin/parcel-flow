package com.parcelflow.domain.ports;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface WatermarkRepositoryPort {
    Optional<ZonedDateTime> getWatermark(String providerName);
    void saveWatermark(String providerName, ZonedDateTime watermark);
}
