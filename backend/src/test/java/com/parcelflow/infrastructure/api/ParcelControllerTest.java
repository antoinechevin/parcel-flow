package com.parcelflow.infrastructure.api;

import com.parcelflow.application.usecases.RetrieveDashboardUseCase;
import com.parcelflow.domain.model.LocationGroup;
import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.model.ParcelStatus;
import com.parcelflow.domain.model.PickupPoint;
import com.parcelflow.domain.model.UrgencyLevel;
import com.parcelflow.infrastructure.api.security.ApiKeyFilter;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() {
        ApiKeyFilter.TEST_KEY_OVERRIDE = "test-key";
    }

    @Test
    void shouldReturnParcels() throws Exception {
        ParcelId id = ParcelId.random();
        PickupPoint pp = new PickupPoint("pp-1", "Relais", "Address", "08:00-19:00");
        when(useCase.retrieve()).thenReturn(List.of(
            new LocationGroup(pp, List.of(
                new Parcel(id, "123", "DHL", LocalDate.now(), ParcelStatus.AVAILABLE, pp)
            ), UrgencyLevel.LOW, 0)
        ));

        mockMvc.perform(get("/api/dashboard")
                .header(ApiKeyFilter.API_KEY_HEADER, "test-key"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].pickupPoint.id").value("pp-1"))
            .andExpect(jsonPath("$[0].parcels[0].id.value").value(id.value().toString()))
            .andExpect(jsonPath("$[0].parcels[0].trackingNumber").value("123"))
            .andExpect(jsonPath("$[0].parcels[0].status").value("AVAILABLE"));
    }
}