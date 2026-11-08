package com.hwresident.application.usecase;

import com.hwresident.domain.model.AccessPermission;
import com.hwresident.domain.repository.AccessPermissionRepository;
import java.util.List;

public class ListAccessPermissionsUseCase {
    private final AccessPermissionRepository permissions;

    public ListAccessPermissionsUseCase(AccessPermissionRepository permissions) {
        this.permissions = permissions;
    }

    public List<AccessPermission> execute() {
        return permissions.findAll();
    }
}
