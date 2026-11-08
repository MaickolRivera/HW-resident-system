package com.hwresident.application.usecase;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hwresident.domain.exception.DomainException;
import com.hwresident.infrastructure.persistence.InMemoryAccessPermissionRepository;
import com.hwresident.infrastructure.persistence.InMemoryResidentRepository;
import com.hwresident.infrastructure.persistence.InMemoryVisitorRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccessPermissionUseCasesTest {

    private static final LocalDateTime FROM = LocalDateTime.of(2026, 11, 7, 10, 0);
    private static final LocalDateTime UNTIL = LocalDateTime.of(2026, 11, 7, 18, 0);
    private static final LocalDateTime INSIDE = LocalDateTime.of(2026, 11, 7, 12, 0);

    private GrantAccessPermissionUseCase grant;
    private CheckAccessUseCase check;

    @BeforeEach
    void setUp() {
        var residents = new InMemoryResidentRepository();
        var visitors = new InMemoryVisitorRepository();
        var permissions = new InMemoryAccessPermissionRepository();

        new RegisterResidentUseCase(residents).execute("Ana Pérez", "123", "Torre 1 - 101");
        new RegisterVisitorUseCase(visitors).execute("Carlos Ruiz", "987");

        grant = new GrantAccessPermissionUseCase(residents, visitors, permissions);
        check = new CheckAccessUseCase(visitors, permissions);
    }

    @Test
    void visitanteConPermisoPuedeIngresarDentroDeLaVentana() {
        grant.execute("123", "987", FROM, UNTIL);
        assertTrue(check.execute("987", INSIDE));
    }

    @Test
    void visitanteConPermisoNoPuedeIngresarFueraDeLaVentana() {
        grant.execute("123", "987", FROM, UNTIL);
        assertFalse(check.execute("987", UNTIL.plusHours(1)));
    }

    @Test
    void visitanteSinPermisoNoPuedeIngresar() {
        assertFalse(check.execute("987", INSIDE));
    }

    @Test
    void visitanteNoRegistradoNoPuedeIngresar() {
        assertFalse(check.execute("000", INSIDE));
    }

    @Test
    void noOtorgaPermisoSiElResidenteNoExiste() {
        assertThrows(DomainException.class, () -> grant.execute("000", "987", FROM, UNTIL));
    }

    @Test
    void noOtorgaPermisoSiElVisitanteNoExiste() {
        assertThrows(DomainException.class, () -> grant.execute("123", "000", FROM, UNTIL));
    }
}
