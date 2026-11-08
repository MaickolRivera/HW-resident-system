package com.hwresident.infrastructure.persistence;

import com.hwresident.domain.model.Visitor;
import com.hwresident.domain.repository.VisitorRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryVisitorRepository implements VisitorRepository {
    private final Map<UUID, Visitor> store = new LinkedHashMap<>();

    @Override
    public Visitor save(Visitor visitor) {
        store.put(visitor.id(), visitor);
        return visitor;
    }

    @Override
    public Optional<Visitor> findByDocumentNumber(String documentNumber) {
        return store.values().stream()
                .filter(v -> v.documentNumber().equals(documentNumber))
                .findFirst();
    }

    @Override
    public List<Visitor> findAll() {
        return new ArrayList<>(store.values());
    }
}
