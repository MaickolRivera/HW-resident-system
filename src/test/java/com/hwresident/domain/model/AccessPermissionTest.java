package com.hwresident.domain.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hwresident.domain.exception.DomainException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AccessPermissionTest {

    private static final LocalDateTime FROM = LocalDateTime.of(2026, 11, 7, 10, 0);
    private static final LocalDateTime UNTIL = LocalDateTime.of(2026, 11, 7, 18, 0);

    private AccessPermission permission() {
        return AccessPermission.grant(UUID.randomUUID(), UUID.randomUUID(), FROM, UNTIL);
    }

    @Test
    void permiteIngresoDentroDeLaVentana() {
        assertTrue(permission().allowsEntryAt(LocalDateTime.of(2026, 11, 7, 12, 0)));
    }

    @Test
    void niegaIngresoFueraDeLaVentana() {
        var p = permission();
        assertFalse(p.allowsEntryAt(FROM.minusMinutes(1)));
        assertFalse(p.allowsEntryAt(UNTIL.plusMinutes(1)));
    }

    @Test
    void niegaIngresoSiFueRevocado() {
        var p = permission();
        p.revoke();
        assertFalse(p.allowsEntryAt(LocalDateTime.of(2026, 11, 7, 12, 0)));
    }

    @Test
    void noPermiteRevocarDosVeces() {
        var p = permission();
        p.revoke();
        assertThrows(DomainException.class, p::revoke);
    }

    @Test
    void rechazaRangoInvalido() {
        assertThrows(DomainException.class,
                () -> AccessPermission.grant(UUID.randomUUID(), UUID.randomUUID(), UNTIL, FROM));
    }
}
