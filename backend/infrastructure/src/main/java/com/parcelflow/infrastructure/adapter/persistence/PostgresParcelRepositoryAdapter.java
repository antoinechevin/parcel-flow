package com.parcelflow.infrastructure.adapter.persistence;

import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.port.ParcelRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PostgresParcelRepositoryAdapter implements ParcelRepository {

    private final JpaParcelRepository jpaParcelRepository;
    private final ParcelMapper parcelMapper;

    public PostgresParcelRepositoryAdapter(JpaParcelRepository jpaParcelRepository, ParcelMapper parcelMapper) {
        this.jpaParcelRepository = jpaParcelRepository;
        this.parcelMapper = parcelMapper;
    }

    @Override
    public void save(Parcel parcel) {
        jpaParcelRepository.save(parcelMapper.toEntity(parcel));
    }

    @Override
    public Optional<Parcel> findById(ParcelId id) {
        return jpaParcelRepository.findById(id.value())
                .map(parcelMapper::toDomain);
    }

    @Override
    public List<Parcel> findAll() {
        return jpaParcelRepository.findAll().stream()
                .map(parcelMapper::toDomain)
                .collect(Collectors.toList());
    }
}
