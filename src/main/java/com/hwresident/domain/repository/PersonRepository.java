package com.hwresident.domain.repository;

import com.hwresident.domain.model.Person;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para todos los actores. Un único repositorio garantiza que un
 * documento identifique a una sola persona, sea residente, externo o guarda.
 */
public interface PersonRepository {
    <T extends Person> T save(T person);
    Optional<Person> findById(UUID id);
    Optional<Person> findByDocumentNumber(String documentNumber);
    List<Person> findAll();

    default <T extends Person> List<T> findAllOfType(Class<T> type) {
        return findAll().stream().filter(type::isInstance).map(type::cast).toList();
    }

    default <T extends Person> Optional<T> findByDocumentNumber(String documentNumber, Class<T> type) {
        return findByDocumentNumber(documentNumber).filter(type::isInstance).map(type::cast);
    }
}
