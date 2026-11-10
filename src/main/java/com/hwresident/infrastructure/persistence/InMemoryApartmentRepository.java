package com.hwresident.infrastructure.persistence;

import com.hwresident.domain.model.Apartment;
import com.hwresident.domain.repository.ApartmentRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryApartmentRepository extends InMemoryRepository<Apartment> implements ApartmentRepository {

    public InMemoryApartmentRepository() {
        super();
    }

    public InMemoryApartmentRepository(Map<UUID, Apartment> store, Runnable onChange) {
        super(store, onChange);
    }

    @Override
    public Apartment save(Apartment apartment) {
        return put(apartment.id(), apartment);
    }

    @Override
    public Optional<Apartment> findById(UUID id) {
        return id == null ? Optional.empty() : get(id);
    }

    @Override
    public List<Apartment> findAll() {
        return values();
    }
}
