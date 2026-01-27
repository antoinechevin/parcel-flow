package com.parcelflow.infrastructure.api;

import com.parcelflow.application.usecases.ExtractParcelUseCase;
import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugExtractionController {

    private final ExtractParcelUseCase useCase;
    private final ParcelRepositoryPort repository;

    public DebugExtractionController(ExtractParcelUseCase useCase, ParcelRepositoryPort repository) {
        this.useCase = useCase;
        this.repository = repository;
    }

    @PostMapping("/extract")
    public Map<String, Object> debugExtract(@RequestBody String emailContent) {
        int before = repository.findAll().size();
        
        useCase.execute(emailContent, java.time.ZonedDateTime.now());
        
        List<Parcel> allParcels = repository.findAll();
        int after = allParcels.size();
        
        boolean created = after > before;
        Parcel lastParcel = created ? allParcels.get(after - 1) : null;

        return Map.of(
            "success", true,
            "parcelCreated", created,
            "totalParcels", after,
            "lastParcel", lastParcel != null ? lastParcel : "None"
        );
    }
}
