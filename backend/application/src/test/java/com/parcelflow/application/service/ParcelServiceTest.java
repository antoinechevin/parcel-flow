package com.parcelflow.application.service;

import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.port.ParcelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParcelServiceTest {

    @Mock
    private ParcelRepository parcelRepository;

    @InjectMocks
    private ParcelService parcelService;

    @Test
    void should_create_parcel() {
        String id = "TRK-123456";
        String label = "Mon Mac";

        parcelService.createParcel(id, label);

        verify(parcelRepository).save(any(Parcel.class));
    }

    @Test
    void should_list_all_parcels() {
        Parcel p1 = Parcel.create(new ParcelId("TRK-1"), "P1");
        Parcel p2 = Parcel.create(new ParcelId("TRK-2"), "P2");
        when(parcelRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Parcel> parcels = parcelService.listParcels();

        assertThat(parcels).hasSize(2);
        assertThat(parcels).contains(p1, p2);
    }
}
