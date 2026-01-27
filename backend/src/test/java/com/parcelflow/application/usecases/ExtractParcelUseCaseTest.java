package com.parcelflow.application.usecases;

import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelMetadata;
import com.parcelflow.domain.model.ParcelStatus;
import com.parcelflow.domain.ports.ParcelExtractionPort;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ExtractParcelUseCaseTest {

    private ExtractParcelUseCase useCase;

    @Mock
    private ParcelExtractionPort extractionPort;

    @Mock
    private ParcelRepositoryPort repositoryPort;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new ExtractParcelUseCase(extractionPort, repositoryPort);
    }

    @Test
    void shouldExtractAndSaveParcel() {
        String emailContent = "Test content";
        ParcelMetadata metadata = new ParcelMetadata(
            "TRK123",
            "DHL",
            LocalDate.now().plusDays(5),
            "Pickup Point"
        );

        when(extractionPort.extract(eq(emailContent), any(ZonedDateTime.class))).thenReturn(Optional.of(metadata));

        useCase.execute(emailContent, ZonedDateTime.now());

        ArgumentCaptor<Parcel> parcelCaptor = ArgumentCaptor.forClass(Parcel.class);
        verify(repositoryPort).save(parcelCaptor.capture());

        Parcel savedParcel = parcelCaptor.getValue();
        assertEquals("TRK123", savedParcel.trackingNumber());
        assertEquals("DHL", savedParcel.carrier());
        assertEquals(ParcelStatus.AVAILABLE, savedParcel.status());
    }

    @Test
    void shouldNotSaveIfParcelAlreadyExists() {
        String emailContent = "Duplicate";
        ParcelMetadata metadata = new ParcelMetadata("DUP123", "DHL", null, null);
        Parcel existingParcel = mock(Parcel.class);

        when(extractionPort.extract(eq(emailContent), any(ZonedDateTime.class))).thenReturn(Optional.of(metadata));
        when(repositoryPort.findByTrackingNumber("DUP123")).thenReturn(Optional.of(existingParcel));

        useCase.execute(emailContent, ZonedDateTime.now());

        verify(repositoryPort, never()).save(any());
    }

    @Test
    void shouldNotSaveIfExtractionFails() {
        when(extractionPort.extract(anyString(), any(ZonedDateTime.class))).thenReturn(Optional.empty());

        useCase.execute("Invalid", ZonedDateTime.now());

        verify(repositoryPort, never()).save(any());
    }

    @Test
    void shouldExecuteWithSpecificAdapter() {
        String emailContent = "Specific Content";
        ZonedDateTime receivedAt = ZonedDateTime.now();
        ParcelExtractionPort specificAdapter = mock(ParcelExtractionPort.class);
        ParcelMetadata metadata = new ParcelMetadata(
            "SPECIFIC123",
            "SpecificCarrier",
            LocalDate.now().plusDays(3),
            "Specific Point"
        );

        when(specificAdapter.extract(emailContent, receivedAt)).thenReturn(Optional.of(metadata));

        useCase.execute(emailContent, receivedAt, specificAdapter);

        verify(specificAdapter).extract(emailContent, receivedAt);
        verify(extractionPort, never()).extract(any(), any()); // Ensure default adapter is NOT used
        
        ArgumentCaptor<Parcel> parcelCaptor = ArgumentCaptor.forClass(Parcel.class);
        verify(repositoryPort).save(parcelCaptor.capture());
        assertEquals("SPECIFIC123", parcelCaptor.getValue().trackingNumber());
    }
}
