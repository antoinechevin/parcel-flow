package com.parcelflow.infrastructure.api;

import com.parcelflow.application.usecases.RetrieveDashboardUseCase;
import com.parcelflow.domain.model.*;
import com.parcelflow.infrastructure.api.security.ApiKeyFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParcelController.class)
class ParcelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RetrieveDashboardUseCase retrieveDashboardUseCase;

    @BeforeEach
    void setupSecurity() {
        ApiKeyFilter.TEST_KEY_OVERRIDE = "test-key";
    }

    @AfterEach
    void cleanupSecurity() {
        ApiKeyFilter.TEST_KEY_OVERRIDE = null;
    }

    @Test
    void shouldReturnDashboard() throws Exception {
        ParcelId id = new ParcelId(UUID.randomUUID());
        PickupPoint pp = new PickupPoint("pp-1", "Relais", "Addr", "08-19");
        List<LocationGroup> groups = List.of(
            new LocationGroup(pp, List.of(
                new Parcel(id, "123", "DHL", LocalDate.now(), ParcelStatus.AVAILABLE, pp, "CODE-99", "http://qr.url", BarcodeType.QR_CODE)
            ), UrgencyLevel.HIGH, 1)
        );

        when(retrieveDashboardUseCase.retrieve()).thenReturn(groups);

        mockMvc.perform(get("/api/dashboard")
                .header("X-API-KEY", "test-key"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].pickupPoint.name").value("Relais"))
            .andExpect(jsonPath("$[0].parcels[0].trackingNumber").value("123"))
            .andExpect(jsonPath("$[0].parcels[0].barcodeType").value("QR_CODE"));
    }
}