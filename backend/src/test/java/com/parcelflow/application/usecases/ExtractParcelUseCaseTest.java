package com.parcelflow.application.usecases;

import com.parcelflow.domain.model.*;
import com.parcelflow.domain.ports.ParcelExtractionPort;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ExtractParcelUseCaseTest {

    private ExtractParcelUseCase useCase;
    private ParcelRepositoryPort repositoryPort;
    private ParcelExtractionPort extractionPort;

    @BeforeEach
    void setUp() {
        repositoryPort = mock(ParcelRepositoryPort.class);
        extractionPort = mock(ParcelExtractionPort.class);
        useCase = new ExtractParcelUseCase(extractionPort, repositoryPort);
    }

    @Test
    void shouldExtractAndSaveParcel() {
        String emailContent = "Sample Content";
        ZonedDateTime receivedAt = ZonedDateTime.now();
        ParcelMetadata metadata = new ParcelMetadata(
            "TRK123", "DHL", null, "Relais 1", null, null, BarcodeType.QR_CODE
        );

        when(extractionPort.extract(eq(emailContent), any(ZonedDateTime.class))).thenReturn(Optional.of(metadata));
        when(repositoryPort.findByTrackingNumber("TRK123")).thenReturn(Optional.empty());

        useCase.execute(emailContent, receivedAt);

        ArgumentCaptor<Parcel> captor = ArgumentCaptor.forClass(Parcel.class);
        verify(repositoryPort).save(captor.capture());
        
        Parcel saved = captor.getValue();
        assertEquals("TRK123", saved.trackingNumber());
        assertEquals("DHL", saved.carrier());
        assertEquals("Relais 1", saved.pickupPoint().name());
        assertEquals(BarcodeType.QR_CODE, saved.barcodeType());
    }

    @Test
    void shouldNotSaveIfExtractionFails() {
        when(extractionPort.extract(anyString(), any(ZonedDateTime.class))).thenReturn(Optional.empty());

        useCase.execute("slop", ZonedDateTime.now());

        verify(repositoryPort, never()).save(any());
    }

    @Test
    void shouldNotSaveIfTrackingNumberEmpty() {
        ParcelMetadata metadata = new ParcelMetadata("", "DHL", null, null, null, null, BarcodeType.NONE);
        when(extractionPort.extract(anyString(), any())).thenReturn(Optional.of(metadata));

        useCase.execute("content", ZonedDateTime.now());

        verify(repositoryPort, never()).save(any());
    }

    @Test
    void shouldHandleSpecificAdapter() {
        String emailContent = "Specific content";
        ZonedDateTime receivedAt = ZonedDateTime.now();
        ParcelMetadata metadata = new ParcelMetadata(
            "SPEC-123", "Carrier", null, "Loc", null, null, BarcodeType.QR_CODE
        );

        ParcelExtractionPort specificAdapter = mock(ParcelExtractionPort.class);
        when(specificAdapter.extract(emailContent, receivedAt)).thenReturn(Optional.of(metadata));
        when(repositoryPort.findByTrackingNumber("SPEC-123")).thenReturn(Optional.empty());

        useCase.execute(emailContent, receivedAt, specificAdapter);

        verify(specificAdapter).extract(emailContent, receivedAt);
        verify(extractionPort, never()).extract(any(), any());
    }

    @Test
    void shouldNormalizePickupPointName() {
        ParcelMetadata metadataOfParcel1 = new ParcelMetadata("TRK001", "C1", null, "  My Local Shop  ", null, null, BarcodeType.QR_CODE);
        ParcelMetadata metadataOfParcel2 = new ParcelMetadata("TRK002", "C2", null, "My LOCAL Shop", null, null, BarcodeType.QR_CODE);

        when(extractionPort.extract(eq("content1"), any())).thenReturn(Optional.of(metadataOfParcel1));
        when(extractionPort.extract(eq("content2"), any())).thenReturn(Optional.of(metadataOfParcel2));
        when(repositoryPort.findByTrackingNumber(anyString())).thenReturn(Optional.empty());

        useCase.execute("content1", ZonedDateTime.now());
        useCase.execute("content2", ZonedDateTime.now());

        ArgumentCaptor<Parcel> captor = ArgumentCaptor.forClass(Parcel.class);
        verify(repositoryPort, times(2)).save(captor.capture());

        String id1 = captor.getAllValues().get(0).pickupPoint().id();
        String id2 = captor.getAllValues().get(1).pickupPoint().id();

        assertEquals(id1, id2, "PickupPoints with same name (ignoring case and whitespace) should have same ID");
    }

    @Test
    void shouldTrimTrackingNumber() {
        ParcelMetadata metadata = new ParcelMetadata("  SPACED-123  ", "Carrier", null, "Loc", null, null, BarcodeType.QR_CODE);
        when(extractionPort.extract(eq("content"), any())).thenReturn(Optional.of(metadata));
        when(repositoryPort.findByTrackingNumber("SPACED-123")).thenReturn(Optional.empty());

        useCase.execute("content", ZonedDateTime.now());

        ArgumentCaptor<Parcel> captor = ArgumentCaptor.forClass(Parcel.class);
        verify(repositoryPort).save(captor.capture());
        assertEquals("SPACED-123", captor.getValue().trackingNumber());
    }

    @Test
    void shouldUpdateExistingParcelWhenTrackingNumberAlreadyExists() {
        String trk = "EXISTING-TRK";
        ParcelId existingId = ParcelId.random();
        Parcel existingParcel = new Parcel(
            existingId, trk, "Old Carrier", null, ParcelStatus.ARCHIVED, 
            new PickupPoint("loc1", "Old Location", "Old Location", null), 
            "old-code", null, BarcodeType.NONE
        );
        
        ParcelMetadata newMetadata = new ParcelMetadata(
            trk, "New Carrier", null, "New Location", "new-code", "new-qr", BarcodeType.QR_CODE
        );

        when(extractionPort.extract(anyString(), any())).thenReturn(Optional.of(newMetadata));
        when(repositoryPort.findByTrackingNumber(trk)).thenReturn(Optional.of(existingParcel));

        useCase.execute("content", ZonedDateTime.now());

        ArgumentCaptor<Parcel> captor = ArgumentCaptor.forClass(Parcel.class);
        verify(repositoryPort).save(captor.capture());
        
        Parcel updated = captor.getValue();
        assertEquals(existingId, updated.id());
        assertEquals("New Carrier", updated.carrier());
        assertEquals(ParcelStatus.AVAILABLE, updated.status(), "Status should be reset to AVAILABLE upon update");
        assertEquals("New Location", updated.pickupPoint().name());
    }
}