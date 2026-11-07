package com.hwresident.infrastructure.persistence;

import com.hwresident.domain.model.Resident;
import com.hwresident.domain.repository.ResidentRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryResidentRepository implements ResidentRepository {
    private final Map<UUID, Resident> store = new LinkedHashMap<>();

    @Override
    public Resident save(Resident resident) {
        store.put(resident.id(), resident);
        return resident;
    }

    @Override
    public Optional<Resident> findByDocumentNumber(String documentNumber) {
        return store.values().stream()
                .filter(r -> r.documentNumber().equals(documentNumber))
                .findFirst();
    }

    @Override
    public List<Resident> findAll() {
        return new ArrayList<>(store.values());
    }
}
