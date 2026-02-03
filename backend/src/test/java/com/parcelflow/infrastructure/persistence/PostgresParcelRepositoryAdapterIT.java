package com.parcelflow.infrastructure.persistence;

import com.parcelflow.domain.model.BarcodeType;
import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.model.ParcelStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(PostgresParcelRepositoryAdapter.class)
@ActiveProfiles("test")
class PostgresParcelRepositoryAdapterIT {

    @Autowired
    private PostgresParcelRepositoryAdapter adapter;

    @Test
    void shouldSaveAndRetrieveParcel() {
        Parcel parcel = new Parcel(
                ParcelId.random(),
                "TRK-TEST-123",
                "LAPOSTE",
                LocalDate.now().plusDays(5),
                ParcelStatus.AVAILABLE,
                null,
                "CODE123",
                "http://qr.url",
                BarcodeType.QR_CODE
        );

        adapter.save(parcel);

        Optional<Parcel> retrieved = adapter.findByTrackingNumber("TRK-TEST-123");

        assertTrue(retrieved.isPresent());
        assertEquals(parcel.trackingNumber(), retrieved.get().trackingNumber());
        assertEquals(ParcelStatus.AVAILABLE, retrieved.get().status());
    }

    @Test
    void shouldUpdateExistingParcel() {
        ParcelId id = ParcelId.random();
        Parcel parcel = new Parcel(
                id,
                "TRK-TEST-456",
                "CHRONOPOST",
                LocalDate.now().plusDays(5),
                ParcelStatus.AVAILABLE,
                null,
                null,
                null,
                BarcodeType.NONE
        );

        adapter.save(parcel);

        Parcel updatedParcel = new Parcel(
                id,
                "TRK-TEST-456",
                "CHRONOPOST",
                LocalDate.now().plusDays(5),
                ParcelStatus.ARCHIVED,
                null,
                null,
                null,
                BarcodeType.NONE
        );

        adapter.save(updatedParcel);

        Optional<Parcel> retrieved = adapter.findByTrackingNumber("TRK-TEST-456");
        assertTrue(retrieved.isPresent());
        assertEquals(ParcelStatus.ARCHIVED, retrieved.get().status());
    }
}
