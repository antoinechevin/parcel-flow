package com.parcelflow.application.service;

import com.parcelflow.application.usecase.CreateParcelUseCase;
import com.parcelflow.application.usecase.ListParcelsUseCase;
import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.port.ParcelRepository;

import java.util.List;

public class ParcelService implements CreateParcelUseCase, ListParcelsUseCase {

    private final ParcelRepository parcelRepository;

    public ParcelService(ParcelRepository parcelRepository) {
        this.parcelRepository = parcelRepository;
    }

    @Override
    public Parcel createParcel(String id, String label) {
        Parcel parcel = Parcel.create(new ParcelId(id), label);
        parcelRepository.save(parcel);
        return parcel;
    }

    @Override
    public List<Parcel> listParcels() {
        return parcelRepository.findAll();
    }
}
