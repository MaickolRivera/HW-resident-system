package com.hwresident.application.usecase;

import com.hwresident.domain.exception.DomainException;
import com.hwresident.domain.model.AccessPermission;
import com.hwresident.domain.model.ExternalPerson;
import com.hwresident.domain.model.Resident;
import com.hwresident.domain.repository.AccessPermissionRepository;
import com.hwresident.domain.repository.PersonRepository;
import java.time.LocalDateTime;

/** El residente autoriza con anticipación a una persona externa a entrar a su apartamento. */
public class GrantAccessPermissionUseCase {
    private final PersonRepository persons;
    private final AccessPermissionRepository permissions;

    public GrantAccessPermissionUseCase(PersonRepository persons, AccessPermissionRepository permissions) {
        this.persons = persons;
        this.permissions = permissions;
    }

    public AccessPermission execute(String residentDocument, String externalDocument,
                                    LocalDateTime validFrom, LocalDateTime validUntil) {
        var resident = persons.findByDocumentNumber(residentDocument, Resident.class)
                .orElseThrow(() -> new DomainException("El residente no está registrado"));
        var external = persons.findByDocumentNumber(externalDocument, ExternalPerson.class)
                .orElseThrow(() -> new DomainException("El visitante no está registrado"));
        return permissions.save(AccessPermission.grant(
                resident.id(), external.id(), resident.apartment().id(), validFrom, validUntil));
    }
}
