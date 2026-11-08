package com.hwresident.application.usecase;

import com.hwresident.domain.exception.DomainException;
import com.hwresident.domain.model.AccessPermission;
import com.hwresident.domain.repository.AccessPermissionRepository;
import java.util.UUID;

public class RevokeAccessPermissionUseCase {
    private final AccessPermissionRepository permissions;

    public RevokeAccessPermissionUseCase(AccessPermissionRepository permissions) {
        this.permissions = permissions;
    }

    public AccessPermission execute(UUID permissionId) {
        var permission = permissions.findById(permissionId)
                .orElseThrow(() -> new DomainException("El permiso no existe"));
        permission.revoke();
        return permissions.save(permission);
    }
}
