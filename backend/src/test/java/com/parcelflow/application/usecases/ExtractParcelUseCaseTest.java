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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
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
            "TRK123", "DHL", LocalDate.now().plusDays(2), "Point Relais"
        );

        when(extractionPort.extract(emailContent)).thenReturn(Optional.of(metadata));

        useCase.execute(emailContent);

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

        when(extractionPort.extract(emailContent)).thenReturn(Optional.of(metadata));
        when(repositoryPort.findByTrackingNumber("DUP123")).thenReturn(Optional.of(existingParcel));

        useCase.execute(emailContent);

        verify(repositoryPort, never()).save(any());
    }

    @Test
    void shouldNotSaveIfExtractionFails() {
        when(extractionPort.extract(anyString())).thenReturn(Optional.empty());

        useCase.execute("Invalid");

        verify(repositoryPort, never()).save(any());
    }
}
