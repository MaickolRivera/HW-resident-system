package com.hwresident.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hwresident.domain.exception.DomainException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** Máquina de estados del registro de la minuta. */
class AccessRecordTest {

    private static final LocalDateTime T0 = LocalDateTime.of(2026, 11, 9, 12, 0);
    private final Guard guard = Guard.create("Pedro", "500");
    private final Apartment apartment = Apartment.create("1", "101");

    private AccessRecord pending() {
        return AccessRecord.request(Visitor.create("Carlos", "987"), apartment, "Visita", null, null, guard, T0);
    }

    @Test
    void nuevoRegistroQuedaEnEspera() {
        var r = pending();
        assertEquals(AccessStatus.PENDING, r.status());
        assertTrue(r.isOpen());
        assertTrue(r.entryTime().isEmpty());
    }

    @Test
    void enEsperaADentroASalida() {
        var r = pending();
        r.admit(T0.plusMinutes(2));
        assertEquals(AccessStatus.INSIDE, r.status());
        assertEquals(T0.plusMinutes(2), r.entryTime().orElseThrow());

        var exitGuard = Guard.create("Sandra", "501");
        r.registerExit(T0.plusHours(1), exitGuard);
        assertEquals(AccessStatus.EXITED, r.status());
        assertEquals(T0.plusHours(1), r.exitTime().orElseThrow());
        assertEquals(exitGuard, r.exitGuard().orElseThrow());
        assertFalse(r.isOpen());
    }

    @Test
    void noRegistraEntradaDosVeces() {
        var r = pending();
        r.admit(T0);
        var e = assertThrows(DomainException.class, () -> r.admit(T0));
        assertEquals("El ingreso ya fue registrado", e.getMessage());
    }

    @Test
    void noDaSalidaAQuienNoHaEntrado() {
        var r = pending();
        var e = assertThrows(DomainException.class, () -> r.registerExit(T0, guard));
        assertEquals("No se puede dar salida a alguien que no ha ingresado", e.getMessage());
    }

    @Test
    void noDaSalidaDosVeces() {
        var r = pending();
        r.admit(T0);
        r.registerExit(T0.plusHours(1), guard);
        assertThrows(DomainException.class, () -> r.registerExit(T0.plusHours(2), guard));
    }

    @Test
    void salidaNoPuedeSerAnteriorALaEntrada() {
        var r = pending();
        r.admit(T0.plusHours(1));
        assertThrows(DomainException.class, () -> r.registerExit(T0, guard));
    }

    @Test
    void rechazadoEsEstadoFinal() {
        var r = pending();
        r.reject();
        assertEquals(AccessStatus.REJECTED, r.status());
        assertFalse(r.isOpen());
        assertThrows(DomainException.class, () -> r.admit(T0));
        assertThrows(DomainException.class, () -> r.registerExit(T0, guard));
        assertThrows(DomainException.class, r::reject);
    }

    @Test
    void noSePuedeRechazarAQuienYaEntro() {
        var r = pending();
        r.admit(T0);
        assertThrows(DomainException.class, r::reject);
    }

    @Test
    void exigeGuardaDestinoYPersona() {
        assertThrows(DomainException.class,
                () -> AccessRecord.request(Visitor.create("C", "1"), apartment, null, null, null, null, T0));
        assertThrows(DomainException.class,
                () -> AccessRecord.request(Visitor.create("C", "1"), null, null, null, null, guard, T0));
        assertThrows(DomainException.class,
                () -> AccessRecord.request(null, apartment, null, null, null, guard, T0));
    }

    @Test
    void camposOpcionalesVaciosQuedanAusentes() {
        var r = AccessRecord.request(Visitor.create("C", "1"), apartment, "  ", null, "", guard, T0);
        assertTrue(r.reason().isEmpty());
        assertTrue(r.correspondence().isEmpty());
        assertTrue(r.vehicle().isEmpty());
    }
}
