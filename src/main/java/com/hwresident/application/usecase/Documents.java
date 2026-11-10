package com.hwresident.application.usecase;

import com.hwresident.domain.exception.DomainException;
import com.hwresident.domain.repository.PersonRepository;

/** Regla compartida por los registros de personas: un documento identifica a una sola persona. */
final class Documents {
    private Documents() {}

    static String ensureAvailable(PersonRepository persons, String documentNumber) {
        var doc = documentNumber == null ? "" : documentNumber.trim();
        persons.findByDocumentNumber(doc).ifPresent(existing -> {
            throw new DomainException("Ya existe una persona con ese documento ("
                    + existing.typeLabel().toLowerCase() + ": " + existing.fullName() + ")");
        });
        return doc;
    }
}
