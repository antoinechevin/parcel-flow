package com.parcelflow.infrastructure.persistence;

import com.parcelflow.domain.Parcel;
import com.parcelflow.domain.ParcelRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryParcelRepositoryTest {

    @Test
    void shouldReturnPreloadedParcels() {
        ParcelRepositoryPort repository = new InMemoryParcelRepository();
        List<Parcel> parcels = repository.findAll();
        assertEquals(3, parcels.size());
    }
}
