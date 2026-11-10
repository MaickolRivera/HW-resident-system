package com.hwresident.application.usecase;

import com.hwresident.domain.model.ExternalPerson;
import com.hwresident.domain.model.Resident;
import com.hwresident.domain.repository.AccessPermissionRepository;
import com.hwresident.domain.repository.PersonRepository;
import java.time.LocalDateTime;

/** Consulta (sin registrar nada) si una persona puede entrar en un momento dado. */
public class CheckAccessUseCase {
    private final PersonRepository persons;
    private final AccessPermissionRepository permissions;

    public CheckAccessUseCase(PersonRepository persons, AccessPermissionRepository permissions) {
        this.persons = persons;
        this.permissions = permissions;
    }

    public boolean execute(String document, LocalDateTime moment) {
        return persons.findByDocumentNumber(document)
                .map(person -> switch (person) {
                    case Resident r -> true;
                    case ExternalPerson e -> permissions.findByExternalId(e.id()).stream()
                            .anyMatch(permission -> permission.allowsEntryAt(moment));
                    default -> false;
                })
                .orElse(false);
    }
}
