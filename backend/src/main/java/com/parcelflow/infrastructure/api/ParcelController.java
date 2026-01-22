package com.parcelflow.infrastructure.api;

import com.parcelflow.application.usecases.RetrieveDashboardUseCase;
import com.parcelflow.domain.model.Parcel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/parcels")
public class ParcelController {

    private final RetrieveDashboardUseCase retrieveDashboardUseCase;

    public ParcelController(RetrieveDashboardUseCase retrieveDashboardUseCase) {
        this.retrieveDashboardUseCase = retrieveDashboardUseCase;
    }

    @GetMapping
    public List<Parcel> getParcels() {
        return retrieveDashboardUseCase.retrieve();
    }
}