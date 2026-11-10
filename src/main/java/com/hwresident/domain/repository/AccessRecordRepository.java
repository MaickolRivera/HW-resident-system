package com.hwresident.domain.repository;

import com.hwresident.domain.model.AccessRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Puerto de la minuta digital. */
public interface AccessRecordRepository {
    AccessRecord save(AccessRecord record);
    Optional<AccessRecord> findById(UUID id);
    List<AccessRecord> findAll();

    /** Registro en espera o dentro de esa persona, si lo hay. */
    default Optional<AccessRecord> findOpenByPerson(UUID personId) {
        return findAll().stream()
                .filter(r -> r.isOpen() && r.person().id().equals(personId))
                .findFirst();
    }
}
