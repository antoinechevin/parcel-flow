package com.parcelflow.infrastructure.api;

import com.parcelflow.application.usecases.ArchiveParcelUseCase;
import com.parcelflow.application.usecases.RetrieveDashboardUseCase;
import com.parcelflow.domain.model.LocationGroup;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ParcelController {

    private final RetrieveDashboardUseCase retrieveDashboardUseCase;
    private final ArchiveParcelUseCase archiveParcelUseCase;

    public ParcelController(RetrieveDashboardUseCase retrieveDashboardUseCase, ArchiveParcelUseCase archiveParcelUseCase) {
        this.retrieveDashboardUseCase = retrieveDashboardUseCase;
        this.archiveParcelUseCase = archiveParcelUseCase;
    }

    @GetMapping("/dashboard")
    public List<LocationGroup> getDashboard() {
        return retrieveDashboardUseCase.retrieve();
    }

    @PostMapping("/parcels/{trackingNumber}/archive")
    public ResponseEntity<Void> archiveParcel(@PathVariable String trackingNumber) {
        archiveParcelUseCase.archive(trackingNumber);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/auth/verify")
    public ResponseEntity<Void> verifyApiKey() {
        return ResponseEntity.ok().build();
    }
}