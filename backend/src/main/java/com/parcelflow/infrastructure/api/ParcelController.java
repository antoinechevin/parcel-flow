package com.parcelflow.infrastructure.api;

import com.parcelflow.application.usecases.RetrieveDashboardUseCase;
import com.parcelflow.domain.model.LocationGroup;
import com.parcelflow.domain.model.Parcel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class ParcelController {

    private final RetrieveDashboardUseCase retrieveDashboardUseCase;

    public ParcelController(RetrieveDashboardUseCase retrieveDashboardUseCase) {
        this.retrieveDashboardUseCase = retrieveDashboardUseCase;
    }

    @GetMapping
    public List<LocationGroup> getDashboard() {
        return retrieveDashboardUseCase.retrieve();
    }
}