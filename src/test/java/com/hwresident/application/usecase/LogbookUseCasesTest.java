package com.hwresident.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hwresident.TestData;
import com.hwresident.application.usecase.EntryResult.Outcome;
import com.hwresident.domain.exception.DomainException;
import com.hwresident.domain.model.AccessStatus;
import com.hwresident.domain.model.ServiceKind;
import com.hwresident.domain.model.ServiceProvider;
import com.hwresident.domain.model.VehicleType;
import com.hwresident.domain.model.Visitor;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Minuta digital: ingreso, autorización, salida, personas dentro e historial. */
class LogbookUseCasesTest {

    private TestData data;
    private RegisterEntryUseCase entry;

    @BeforeEach
    void setUp() {
        data = new TestData();
        entry = data.registerEntry();
    }

    private EntryRequest visit(String document, UUID destination) {
        return EntryRequest.of("500", document, destination, "Visita");
    }

    @Test
    void residenteEntraDirectoASuApartamento() {
        var result = entry.execute(visit("123", null));

        assertEquals(Outcome.RESIDENT_ENTERED, result.outcome());
        assertEquals(AccessStatus.INSIDE, result.record().status());
        assertEquals(data.t1101, result.record().destination());
        assertEquals(TestData.NOW, result.record().entryTime().orElseThrow());
        assertEquals(data.guard, result.record().entryGuard());
    }

    @Test
    void externoConPermisoVigenteEntraDirecto() {
        data.grant().execute("123", "987", TestData.NOW.minusHours(1), TestData.NOW.plusHours(1));
        var result = entry.execute(visit("987", data.t1101.id()));

        assertEquals(Outcome.PRE_AUTHORIZED, result.outcome());
        assertEquals(AccessStatus.INSIDE, result.record().status());
    }

    @Test
    void permisoParaOtroApartamentoNoSirve() {
        data.grant().execute("123", "987", TestData.NOW.minusHours(1), TestData.NOW.plusHours(1));
        var result = entry.execute(visit("987", data.t2302.id()));

        assertEquals(Outcome.AWAITING_AUTHORIZATION, result.outcome());
        assertEquals(AccessStatus.PENDING, result.record().status());
    }

    @Test
    void externoSinPermisoQuedaEnEsperaYLuegoSeAutoriza() {
        var pending = entry.execute(visit("987", data.t1101.id())).record();
        assertEquals(AccessStatus.PENDING, pending.status());

        data.clock.advance(Duration.ofMinutes(3));
        var admitted = data.resolve().execute(pending.id(), true);

        assertEquals(AccessStatus.INSIDE, admitted.status());
        assertEquals(TestData.NOW.plusMinutes(3), admitted.entryTime().orElseThrow());
    }

    @Test
    void elResidenteRechazaElIngreso() {
        var pending = entry.execute(visit("987", data.t1101.id())).record();
        var rejected = data.resolve().execute(pending.id(), false);

        assertEquals(AccessStatus.REJECTED, rejected.status());
        // rechazado no bloquea una nueva solicitud
        assertEquals(Outcome.AWAITING_AUTHORIZATION, entry.execute(visit("987", data.t1101.id())).outcome());
    }

    @Test
    void personaNoRegistradaSeRegistraAlLlegar() {
        var request = new EntryRequest("500", "444", "Felipe Rojas", ServiceKind.CONTRACTOR, "Pinturas JR",
                "Pintura", data.t2302.id(), "Obra", "xyz98f", VehicleType.MOTORCYCLE, null);
        var result = entry.execute(request);

        var felipe = assertInstanceOf(ServiceProvider.class, data.persons.findByDocumentNumber("444").orElseThrow());
        assertEquals("Pinturas JR", felipe.company());
        assertEquals(Outcome.AWAITING_AUTHORIZATION, result.outcome());
        assertEquals("Moto XYZ98F", result.record().vehicle().orElseThrow().label());
    }

    @Test
    void personaNoRegistradaSinTipoQuedaComoVisitante() {
        var request = new EntryRequest("500", "445", "Laura Díaz", null, null, null,
                data.t1101.id(), null, null, null, "Paquete");
        entry.execute(request);
        assertInstanceOf(Visitor.class, data.persons.findByDocumentNumber("445").orElseThrow());
    }

    @Test
    void personaNoRegistradaSinNombreNoSeRegistra() {
        var e = assertThrows(DomainException.class, () -> entry.execute(visit("444", data.t1101.id())));
        assertEquals("Persona no registrada: ingrese su nombre para registrarla", e.getMessage());
        assertTrue(data.persons.findByDocumentNumber("444").isEmpty());
    }

    @Test
    void sinDestinoNoSeRegistraANadie() {
        var request = new EntryRequest("500", "444", "Felipe", null, null, null, null, null, null, null, null);
        assertThrows(DomainException.class, () -> entry.execute(request));
        assertTrue(data.persons.findByDocumentNumber("444").isEmpty());
        assertTrue(data.records.findAll().isEmpty());
    }

    @Test
    void exigeGuardaDeTurno() {
        var e = assertThrows(DomainException.class,
                () -> entry.execute(EntryRequest.of("", "123", null, null)));
        assertEquals("Seleccione el guarda de turno", e.getMessage());
        // un residente no puede actuar como guarda
        assertThrows(DomainException.class, () -> entry.execute(EntryRequest.of("123", "987", data.t1101.id(), null)));
    }

    @Test
    void noRegistraDosIngresosAbiertosDeLaMismaPersona() {
        entry.execute(visit("123", null));
        var e = assertThrows(DomainException.class, () -> entry.execute(visit("123", null)));
        assertTrue(e.getMessage().contains("ya se encuentra dentro"));

        entry.execute(visit("987", data.t1101.id()));
        e = assertThrows(DomainException.class, () -> entry.execute(visit("987", data.t1101.id())));
        assertTrue(e.getMessage().contains("en espera"));
    }

    @Test
    void registraSalidaConHoraYGuarda() {
        var record = entry.execute(visit("123", null)).record();
        data.clock.advance(Duration.ofHours(2));

        var exited = data.registerExit().execute(record.id(), "500");

        assertEquals(AccessStatus.EXITED, exited.status());
        assertEquals(TestData.NOW.plusHours(2), exited.exitTime().orElseThrow());
        assertEquals(data.guard, exited.exitGuard().orElseThrow());
        // tras salir puede volver a entrar
        assertEquals(Outcome.RESIDENT_ENTERED, entry.execute(visit("123", null)).outcome());
    }

    @Test
    void noDaSalidaAQuienEstaEnEspera() {
        var pending = entry.execute(visit("987", data.t1101.id())).record();
        assertThrows(DomainException.class, () -> data.registerExit().execute(pending.id(), "500"));
    }

    @Test
    void personasDentroSonLosExternosSinSalida() {
        entry.execute(visit("123", null));                                            // residente dentro
        var carlos = entry.execute(visit("987", data.t1101.id())).record();
        data.resolve().execute(carlos.id(), true);                                    // externo dentro
        var diana = entry.execute(visit("300", data.t2302.id())).record();            // en espera
        assertEquals(AccessStatus.PENDING, diana.status());

        var inside = data.listInside().execute(false);
        assertEquals(1, inside.size());
        assertEquals("Carlos Ruiz", inside.get(0).person().fullName());
        assertEquals(2, data.listInside().execute(true).size());

        data.registerExit().execute(carlos.id(), "500");
        assertTrue(data.listInside().execute(false).isEmpty());
    }

    @Test
    void historialFiltraPorDocumentoYApartamento() {
        entry.execute(visit("123", null));
        data.clock.advance(Duration.ofMinutes(5));
        entry.execute(visit("987", data.t1101.id()));
        data.clock.advance(Duration.ofMinutes(5));
        entry.execute(visit("300", data.t2302.id()));

        var history = data.history();
        assertEquals(3, history.execute(null, null, null).size());
        assertEquals("Diana Torres", history.execute("", null, null).get(0).person().fullName()); // más reciente primero
        assertEquals(1, history.execute("987", null, null).size());
        assertEquals(2, history.execute(null, data.t1101.id(), null).size());
        assertEquals(0, history.execute("987", data.t2302.id(), null).size());
        assertEquals(3, history.execute(null, null, TestData.NOW.toLocalDate()).size());
        assertEquals(0, history.execute(null, null, TestData.NOW.toLocalDate().minusDays(1)).size());
    }
}
