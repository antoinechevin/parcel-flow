package com.parcelflow.infrastructure.persistence;

import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.model.ParcelStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryParcelRepositoryTest {

    @Test
    void shouldUpsertParcelOnSave() {
        InMemoryParcelRepository repository = new InMemoryParcelRepository();
        Parcel parcel1 = new Parcel(ParcelId.random(), "TRK123", "DHL", LocalDate.now(), ParcelStatus.AVAILABLE, null, null, null);
        
        // First save
        repository.save(parcel1);
        assertEquals(1, repository.findAll().size());

        // Second save with same tracking number (update scenario)
        Parcel parcel2 = new Parcel(ParcelId.random(), "TRK123", "DHL", LocalDate.now().plusDays(1), ParcelStatus.PICKED_UP, null, null, null);
        repository.save(parcel2);

        List<Parcel> all = repository.findAll();
        assertEquals(1, all.size(), "Should have only 1 parcel after upsert");
        assertEquals(ParcelStatus.PICKED_UP, all.get(0).status(), "Should have updated the status");
        assertEquals(LocalDate.now().plusDays(1), all.get(0).deadline(), "Should have updated the deadline");
    }
}
