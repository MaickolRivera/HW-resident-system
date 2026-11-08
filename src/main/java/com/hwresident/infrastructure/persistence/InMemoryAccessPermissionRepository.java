package com.hwresident.infrastructure.persistence;

import com.hwresident.domain.model.AccessPermission;
import com.hwresident.domain.repository.AccessPermissionRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryAccessPermissionRepository implements AccessPermissionRepository {
    private final Map<UUID, AccessPermission> store = new LinkedHashMap<>();

    @Override
    public AccessPermission save(AccessPermission permission) {
        store.put(permission.id(), permission);
        return permission;
    }

    @Override
    public Optional<AccessPermission> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<AccessPermission> findByVisitorId(UUID visitorId) {
        return store.values().stream()
                .filter(p -> p.visitorId().equals(visitorId))
                .toList();
    }

    @Override
    public List<AccessPermission> findAll() {
        return new ArrayList<>(store.values());
    }
}
