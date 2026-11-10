package com.hwresident.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hwresident.TestData;
import com.hwresident.domain.exception.DomainException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccessPermissionUseCasesTest {

    private static final LocalDateTime FROM = LocalDateTime.of(2026, 11, 7, 10, 0);
    private static final LocalDateTime UNTIL = LocalDateTime.of(2026, 11, 7, 18, 0);
    private static final LocalDateTime INSIDE = LocalDateTime.of(2026, 11, 7, 12, 0);

    private TestData data;
    private GrantAccessPermissionUseCase grant;
    private CheckAccessUseCase check;

    @BeforeEach
    void setUp() {
        data = new TestData();
        grant = data.grant();
        check = new CheckAccessUseCase(data.persons, data.permissions);
    }

    @Test
    void elPermisoQuedaAsociadoAlApartamentoDelResidente() {
        var permission = grant.execute("123", "987", FROM, UNTIL);
        assertEquals(data.t1101.id(), permission.apartmentId());
    }

    @Test
    void visitanteConPermisoPuedeIngresarDentroDeLaVentana() {
        grant.execute("123", "987", FROM, UNTIL);
        assertTrue(check.execute("987", INSIDE));
    }

    @Test
    void prestadorDeServicioTambienPuedeRecibirPermiso() {
        grant.execute("123", "300", FROM, UNTIL);
        assertTrue(check.execute("300", INSIDE));
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
    void residenteSiemprePuedeIngresar() {
        assertTrue(check.execute("123", INSIDE));
    }

    @Test
    void documentoNoRegistradoNoPuedeIngresar() {
        assertFalse(check.execute("000", INSIDE));
    }

    @Test
    void noOtorgaPermisoSiElResidenteNoExiste() {
        assertThrows(DomainException.class, () -> grant.execute("000", "987", FROM, UNTIL));
        // un visitante no puede otorgar permisos
        assertThrows(DomainException.class, () -> grant.execute("987", "300", FROM, UNTIL));
    }

    @Test
    void noOtorgaPermisoSiElVisitanteNoExiste() {
        assertThrows(DomainException.class, () -> grant.execute("123", "000", FROM, UNTIL));
    }
}
