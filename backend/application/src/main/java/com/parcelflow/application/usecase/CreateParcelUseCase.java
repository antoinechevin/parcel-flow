package com.parcelflow.application.usecase;

import com.parcelflow.domain.model.Parcel;
import java.util.List;

public interface CreateParcelUseCase {
    Parcel createParcel(String id, String label);
}
