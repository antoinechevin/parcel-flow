package com.parcelflow.application;

import com.parcelflow.domain.Parcel;
import com.parcelflow.domain.ParcelRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetrieveDashboardUseCaseTest {

    @Mock
    private ParcelRepositoryPort repository;

    @Test
    void shouldReturnParcelsFromRepository() {
        RetrieveDashboardUseCase useCase = new RetrieveDashboardUseCase(repository);
        List<Parcel> expectedParcels = Collections.emptyList();
        
        when(repository.findAll()).thenReturn(expectedParcels);

        List<Parcel> actualParcels = useCase.retrieve();

        assertEquals(expectedParcels, actualParcels);
    }
}
