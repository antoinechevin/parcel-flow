package com.parcelflow.infrastructure.persistence;

import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.model.PickupPoint;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Primary
public class PostgresParcelRepositoryAdapter implements ParcelRepositoryPort {

    private final SpringDataParcelRepository repository;

    public PostgresParcelRepositoryAdapter(SpringDataParcelRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Parcel> findAll() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Parcel> findByTrackingNumber(String trackingNumber) {
        return repository.findByTrackingNumber(trackingNumber)
                .map(this::toDomain);
    }

    @Override
    public void save(Parcel parcel) {
        repository.save(toEntity(parcel));
    }

    @Override
    public void saveAll(List<Parcel> parcels) {
        List<ParcelEntity> entities = parcels.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        repository.saveAll(entities);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    private Parcel toDomain(ParcelEntity entity) {
        PickupPoint pickupPoint = null;
        if (entity.getPickupPointId() != null) {
            pickupPoint = new PickupPoint(
                    entity.getPickupPointId(),
                    entity.getPickupPointName(),
                    entity.getPickupPointAddress(),
                    entity.getPickupPointOpeningHours()
            );
        }

        return new Parcel(
                new ParcelId(entity.getId()),
                entity.getTrackingNumber(),
                entity.getCarrier(),
                entity.getDeadline(),
                entity.getStatus(),
                pickupPoint,
                entity.getPickupCode(),
                entity.getQrCodeUrl(),
                entity.getBarcodeType()
        );
    }

    private ParcelEntity toEntity(Parcel domain) {
        ParcelEntity entity = new ParcelEntity();
        entity.setId(domain.id().value());
        entity.setTrackingNumber(domain.trackingNumber());
        entity.setCarrier(domain.carrier());
        entity.setDeadline(domain.deadline());
        entity.setStatus(domain.status());

        if (domain.pickupPoint() != null) {
            entity.setPickupPointId(domain.pickupPoint().id());
            entity.setPickupPointName(domain.pickupPoint().name());
            entity.setPickupPointAddress(domain.pickupPoint().rawAddress());
            entity.setPickupPointOpeningHours(domain.pickupPoint().openingHours());
        }

        entity.setPickupCode(domain.pickupCode());
        entity.setQrCodeUrl(domain.qrCodeUrl());
        entity.setBarcodeType(domain.barcodeType());

        return entity;
    }
}
