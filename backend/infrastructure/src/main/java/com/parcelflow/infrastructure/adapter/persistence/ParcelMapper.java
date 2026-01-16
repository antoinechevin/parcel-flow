package com.parcelflow.infrastructure.adapter.persistence;

import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.model.ParcelStatus;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@Component
public class ParcelMapper {

    public ParcelEntity toEntity(Parcel parcel) {
        return new ParcelEntity(
                parcel.getId().value(),
                parcel.getLabel(),
                parcel.getStatus().name()
        );
    }

    public Parcel toDomain(ParcelEntity entity) {
        // Using reflection to bypass private constructor or adding a static reconstruct factory method in Domain
        // Ideally, Domain should expose a reconstruction factory method
        // For now, let's assume we can use the create method but that validates input and sets status to CREATED...
        // We need a way to reconstitute an existing object.
        // Let's Add a reconstruction constructor or factory to Parcel in Domain Layer?
        // OR use reflection here to be cleaner regarding Domain purity?
        // Let's add a package-private constructor in Parcel that takes all fields, and place a ReconstitutionFactory in the same package?
        // Or just use reflection here as it's an infrastructure concern.
        
        try {
            Constructor<Parcel> constructor = Parcel.class.getDeclaredConstructor(ParcelId.class, String.class, ParcelStatus.class);
            constructor.setAccessible(true);
            return constructor.newInstance(
                    new ParcelId(entity.getId()),
                    entity.getLabel(),
                    ParcelStatus.valueOf(entity.getStatus())
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to map ParcelEntity to Parcel", e);
        }
    }
}
