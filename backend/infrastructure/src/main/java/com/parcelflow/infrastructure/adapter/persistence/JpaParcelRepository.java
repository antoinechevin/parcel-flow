package com.parcelflow.infrastructure.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaParcelRepository extends JpaRepository<ParcelEntity, String> {
}
