package com.hwresident.application.usecase;

import com.hwresident.domain.exception.DomainException;
import com.hwresident.domain.model.AccessPermission;
import com.hwresident.domain.repository.AccessPermissionRepository;
import com.hwresident.domain.repository.ResidentRepository;
import com.hwresident.domain.repository.VisitorRepository;
import java.time.LocalDateTime;

public class GrantAccessPermissionUseCase {
    private final ResidentRepository residents;
    private final VisitorRepository visitors;
    private final AccessPermissionRepository permissions;

    public GrantAccessPermissionUseCase(ResidentRepository residents, VisitorRepository visitors, AccessPermissionRepository permissions) {
        this.residents = residents;
        this.visitors = visitors;
        this.permissions = permissions;
    }

    public AccessPermission execute(String residentDocument, String visitorDocument, LocalDateTime validFrom, LocalDateTime validUntil) {
        var resident = residents.findByDocumentNumber(residentDocument)
                .orElseThrow(() -> new DomainException("El residente no está registrado"));
        var visitor = visitors.findByDocumentNumber(visitorDocument)
                .orElseThrow(() -> new DomainException("El visitante no está registrado"));
        return permissions.save(AccessPermission.grant(resident.id(), visitor.id(), validFrom, validUntil));
    }
}
