package com.hwresident.infrastructure.persistence;

import com.hwresident.domain.model.AccessPermission;
import com.hwresident.domain.repository.AccessPermissionRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryAccessPermissionRepository extends InMemoryRepository<AccessPermission>
        implements AccessPermissionRepository {

    public InMemoryAccessPermissionRepository() {
        super();
    }

    public InMemoryAccessPermissionRepository(Map<UUID, AccessPermission> store, Runnable onChange) {
        super(store, onChange);
    }

    @Override
    public AccessPermission save(AccessPermission permission) {
        return put(permission.id(), permission);
    }

    @Override
    public Optional<AccessPermission> findById(UUID id) {
        return get(id);
    }

    @Override
    public List<AccessPermission> findByExternalId(UUID externalId) {
        return values().stream().filter(p -> p.externalId().equals(externalId)).toList();
    }

    @Override
    public List<AccessPermission> findAll() {
        return values();
    }
}
