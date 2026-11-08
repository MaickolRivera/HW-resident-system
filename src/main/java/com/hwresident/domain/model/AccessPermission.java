package com.hwresident.domain.model;

import com.hwresident.domain.exception.DomainException;
import java.time.LocalDateTime;
import java.util.UUID;

public class AccessPermission {
    private final UUID id;
    private final UUID residentId;
    private final UUID visitorId;
    private final LocalDateTime validFrom;
    private final LocalDateTime validUntil;
    private boolean revoked;

    public AccessPermission(UUID id, UUID residentId, UUID visitorId, LocalDateTime validFrom, LocalDateTime validUntil, boolean revoked) {
        if (residentId == null) throw new DomainException("El residente es obligatorio");
        if (visitorId == null) throw new DomainException("El visitante es obligatorio");
        if (validFrom == null || validUntil == null) throw new DomainException("El rango de fechas es obligatorio");
        if (!validUntil.isAfter(validFrom)) {
            throw new DomainException("La fecha final debe ser posterior a la inicial");
        }
        this.id = id;
        this.residentId = residentId;
        this.visitorId = visitorId;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.revoked = revoked;
    }

    public static AccessPermission grant(UUID residentId, UUID visitorId,
                                         LocalDateTime validFrom, LocalDateTime validUntil) {
        return new AccessPermission(UUID.randomUUID(), residentId, visitorId, validFrom, validUntil, false);
    }

    public boolean allowsEntryAt(LocalDateTime moment) {
        return !revoked && !moment.isBefore(validFrom) && !moment.isAfter(validUntil);
    }

    public void revoke() {
        if (revoked) throw new DomainException("El permiso ya fue revocado");
        this.revoked = true;
    }

    public UUID id() { return id; }
    public UUID residentId() { return residentId; }
    public UUID visitorId() { return visitorId; }
    public LocalDateTime validFrom() { return validFrom; }
    public LocalDateTime validUntil() { return validUntil; }
    public boolean isRevoked() { return revoked; }
}
