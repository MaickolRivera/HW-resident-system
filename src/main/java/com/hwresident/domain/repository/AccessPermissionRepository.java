package com.hwresident.domain.repository;

import com.hwresident.domain.model.AccessPermission;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccessPermissionRepository {
    AccessPermission save(AccessPermission permission);
    Optional<AccessPermission> findById(UUID id);
    List<AccessPermission> findByExternalId(UUID externalId);
    List<AccessPermission> findAll();
}
