package com.parcelflow.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataParcelRepository extends JpaRepository<ParcelEntity, UUID> {
    Optional<ParcelEntity> findByTrackingNumber(String trackingNumber);
}
