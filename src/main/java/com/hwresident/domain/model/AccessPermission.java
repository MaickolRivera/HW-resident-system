package com.hwresident.domain.model;

import com.hwresident.domain.exception.DomainException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Autorización anticipada: un residente permite que una persona externa entre
 * a su apartamento durante una ventana de tiempo.
 */
public class AccessPermission implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID residentId;
    private final UUID externalId;
    private final UUID apartmentId;
    private final LocalDateTime validFrom;
    private final LocalDateTime validUntil;
    private boolean revoked;

    public AccessPermission(UUID id, UUID residentId, UUID externalId, UUID apartmentId,
                            LocalDateTime validFrom, LocalDateTime validUntil, boolean revoked) {
        if (residentId == null) throw new DomainException("El residente es obligatorio");
        if (externalId == null) throw new DomainException("El visitante es obligatorio");
        if (apartmentId == null) throw new DomainException("El apartamento destino es obligatorio");
        if (validFrom == null || validUntil == null) throw new DomainException("El rango de fechas es obligatorio");
        if (!validUntil.isAfter(validFrom)) {
            throw new DomainException("La fecha final debe ser posterior a la inicial");
        }
        this.id = id;
        this.residentId = residentId;
        this.externalId = externalId;
        this.apartmentId = apartmentId;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.revoked = revoked;
    }

    public static AccessPermission grant(UUID residentId, UUID externalId, UUID apartmentId,
                                         LocalDateTime validFrom, LocalDateTime validUntil) {
        return new AccessPermission(UUID.randomUUID(), residentId, externalId, apartmentId, validFrom, validUntil, false);
    }

    public boolean allowsEntryAt(LocalDateTime moment) {
        return !revoked && !moment.isBefore(validFrom) && !moment.isAfter(validUntil);
    }

    /** Permite el ingreso a ese apartamento en ese momento. */
    public boolean allowsEntryTo(UUID apartment, LocalDateTime moment) {
        return apartmentId.equals(apartment) && allowsEntryAt(moment);
    }

    public void revoke() {
        if (revoked) throw new DomainException("El permiso ya fue revocado");
        this.revoked = true;
    }

    public UUID id() { return id; }
    public UUID residentId() { return residentId; }
    public UUID externalId() { return externalId; }
    public UUID apartmentId() { return apartmentId; }
    public LocalDateTime validFrom() { return validFrom; }
    public LocalDateTime validUntil() { return validUntil; }
    public boolean isRevoked() { return revoked; }
}
