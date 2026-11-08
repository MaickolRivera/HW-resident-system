package com.hwresident.application.usecase;

import com.hwresident.domain.repository.AccessPermissionRepository;
import com.hwresident.domain.repository.VisitorRepository;
import java.time.LocalDateTime;

public class CheckAccessUseCase {
    private final VisitorRepository visitors;
    private final AccessPermissionRepository permissions;

    public CheckAccessUseCase(VisitorRepository visitors, AccessPermissionRepository permissions) {
        this.visitors = visitors;
        this.permissions = permissions;
    }

    public boolean execute(String visitorDocument, LocalDateTime moment) {
        return visitors.findByDocumentNumber(visitorDocument)
                .map(visitor -> permissions.findByVisitorId(visitor.id()).stream()
                        .anyMatch(permission -> permission.allowsEntryAt(moment)))
                .orElse(false);
    }
}
