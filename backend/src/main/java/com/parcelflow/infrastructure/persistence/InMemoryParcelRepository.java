package com.parcelflow.infrastructure.persistence;

import com.parcelflow.domain.Parcel;
import com.parcelflow.domain.ParcelRepositoryPort;
import com.parcelflow.domain.ParcelStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class InMemoryParcelRepository implements ParcelRepositoryPort {

    @Override
    public List<Parcel> findAll() {
        return List.of(
            new Parcel(UUID.randomUUID(), "SHOES-123", LocalDate.now().plusDays(1), ParcelStatus.AVAILABLE),
            new Parcel(UUID.randomUUID(), "BOOK-456", LocalDate.now().plusDays(2), ParcelStatus.AVAILABLE),
            new Parcel(UUID.randomUUID(), "HAT-789", LocalDate.now().minusDays(1), ParcelStatus.PICKED_UP)
        );
    }
}
