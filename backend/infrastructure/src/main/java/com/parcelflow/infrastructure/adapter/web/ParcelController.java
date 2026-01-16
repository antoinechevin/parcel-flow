package com.parcelflow.infrastructure.adapter.web;

import com.parcelflow.application.usecase.CreateParcelUseCase;
import com.parcelflow.application.usecase.ListParcelsUseCase;
import com.parcelflow.domain.model.Parcel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parcels")
public class ParcelController {

    private final CreateParcelUseCase createParcelUseCase;
    private final ListParcelsUseCase listParcelsUseCase;

    public ParcelController(CreateParcelUseCase createParcelUseCase, ListParcelsUseCase listParcelsUseCase) {
        this.createParcelUseCase = createParcelUseCase;
        this.listParcelsUseCase = listParcelsUseCase;
    }

    @PostMapping
    public ResponseEntity<ParcelResponse> createParcel(@RequestBody CreateParcelRequest request) {
        try {
            Parcel parcel = createParcelUseCase.createParcel(request.id(), request.label());
            return ResponseEntity.status(HttpStatus.CREATED).body(ParcelResponse.from(parcel));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public List<ParcelResponse> listParcels() {
        return listParcelsUseCase.listParcels().stream()
                .map(ParcelResponse::from)
                .collect(Collectors.toList());
    }
}
