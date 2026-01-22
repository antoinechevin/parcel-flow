package com.parcelflow.infrastructure.api;

import com.parcelflow.application.usecases.RetrieveDashboardUseCase;
import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.model.ParcelStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ParcelController.class)
class ParcelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RetrieveDashboardUseCase useCase;

    @Test
    void shouldReturnParcels() throws Exception {
        ParcelId id = ParcelId.random();
        when(useCase.retrieve()).thenReturn(List.of(
            new Parcel(id, "123", LocalDate.now(), ParcelStatus.AVAILABLE)
        ));

        mockMvc.perform(get("/api/parcels"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id.value").value(id.value().toString()))
            .andExpect(jsonPath("$[0].trackingNumber").value("123"))
            .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
    }
}