package com.hwresident.infrastructure.persistence;

import com.hwresident.domain.model.Person;
import com.hwresident.domain.repository.PersonRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryPersonRepository extends InMemoryRepository<Person> implements PersonRepository {

    public InMemoryPersonRepository() {
        super();
    }

    public InMemoryPersonRepository(Map<UUID, Person> store, Runnable onChange) {
        super(store, onChange);
    }

    @Override
    public <T extends Person> T save(T person) {
        return put(person.id(), person);
    }

    @Override
    public Optional<Person> findById(UUID id) {
        return get(id);
    }

    @Override
    public Optional<Person> findByDocumentNumber(String documentNumber) {
        var doc = documentNumber == null ? "" : documentNumber.trim();
        return values().stream().filter(p -> p.documentNumber().equals(doc)).findFirst();
    }

    @Override
    public List<Person> findAll() {
        return values();
    }
}
