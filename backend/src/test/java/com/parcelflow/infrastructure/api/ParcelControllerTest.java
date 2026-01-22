package com.parcelflow.infrastructure.api;

import com.parcelflow.application.RetrieveDashboardUseCase;
import com.parcelflow.domain.Parcel;
import com.parcelflow.domain.ParcelStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
        UUID id = UUID.randomUUID();
        when(useCase.retrieve()).thenReturn(List.of(
            new Parcel(id, "123", LocalDate.now(), ParcelStatus.AVAILABLE)
        ));

        mockMvc.perform(get("/api/parcels"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(id.toString()))
            .andExpect(jsonPath("$[0].trackingNumber").value("123"))
            .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
    }
}
