package com.hwresident.presentation.gui.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hwresident.application.usecase.GrantAccessPermissionUseCase;
import com.hwresident.application.usecase.ListAccessPermissionsUseCase;
import com.hwresident.application.usecase.ListResidentsUseCase;
import com.hwresident.application.usecase.ListVisitorsUseCase;
import com.hwresident.application.usecase.RegisterResidentUseCase;
import com.hwresident.application.usecase.RegisterVisitorUseCase;
import com.hwresident.application.usecase.RevokeAccessPermissionUseCase;
import com.hwresident.infrastructure.persistence.InMemoryAccessPermissionRepository;
import com.hwresident.infrastructure.persistence.InMemoryResidentRepository;
import com.hwresident.infrastructure.persistence.InMemoryVisitorRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PermissionsPresenterTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 9, 12, 0);
    private static final Clock CLOCK = Clock.fixed(NOW.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);

    private static class FakeView implements PermissionsView {
        Optional<PersonOption> resident = Optional.empty();
        Optional<PersonOption> visitor = Optional.empty();
        LocalDateTime from = NOW.minusHours(1), until = NOW.plusHours(3);
        Optional<PermissionRow> selected = Optional.empty();
        boolean confirm = true;
        List<PersonOption> residentOptions = List.of(), visitorOptions = List.of();
        List<PermissionRow> rows = List.of();
        String success, error;

        public Optional<PersonOption> selectedResident() { return resident; }
        public Optional<PersonOption> selectedVisitor() { return visitor; }
        public LocalDateTime validFrom() { return from; }
        public LocalDateTime validUntil() { return until; }
        public Optional<PermissionRow> selectedPermission() { return selected; }
        public void showResidentOptions(List<PersonOption> o) { residentOptions = o; }
        public void showVisitorOptions(List<PersonOption> o) { visitorOptions = o; }
        public void showPermissions(List<PermissionRow> r) { rows = r; }
        public boolean confirmRevoke(PermissionRow p) { return confirm; }
        public void showSuccess(String m) { success = m; error = null; }
        public void showError(String m) { error = m; success = null; }
    }

    private FakeView view;
    private PermissionsPresenter presenter;

    @BeforeEach
    void setUp() {
        var residents = new InMemoryResidentRepository();
        var visitors = new InMemoryVisitorRepository();
        var permissions = new InMemoryAccessPermissionRepository();
        new RegisterResidentUseCase(residents).execute("Ana Pérez", "123", "Torre 1 - 101");
        new RegisterVisitorUseCase(visitors).execute("Carlos Ruiz", "987");

        view = new FakeView();
        presenter = new PermissionsPresenter(view,
                new ListResidentsUseCase(residents), new ListVisitorsUseCase(visitors),
                new GrantAccessPermissionUseCase(residents, visitors, permissions),
                new RevokeAccessPermissionUseCase(permissions),
                new ListAccessPermissionsUseCase(permissions), CLOCK);
        presenter.onShow();
        view.resident = Optional.of(view.residentOptions.get(0));
        view.visitor = Optional.of(view.visitorOptions.get(0));
    }

    @Test
    void onShowCargaLasOpciones() {
        assertEquals("123", view.residentOptions.get(0).documentNumber());
        assertEquals("Carlos Ruiz (987)", view.visitorOptions.get(0).label());
    }

    @Test
    void otorgaPermisoVigenteConNombresResueltos() {
        presenter.onGrant();

        assertEquals("Permiso otorgado", view.success);
        var row = view.rows.get(0);
        assertEquals("Ana Pérez (123) - Torre 1 - 101", row.residentLabel());
        assertEquals("Carlos Ruiz (987)", row.visitorLabel());
        assertEquals(PermissionStatus.ACTIVE, row.status());
    }

    @Test
    void calculaEstadosProgramadoYVencido() {
        view.from = NOW.plusDays(1); view.until = NOW.plusDays(2);
        presenter.onGrant();
        view.from = NOW.minusDays(2); view.until = NOW.minusDays(1);
        presenter.onGrant();

        assertEquals(PermissionStatus.SCHEDULED, view.rows.get(0).status());
        assertEquals(PermissionStatus.EXPIRED, view.rows.get(1).status());
    }

    @Test
    void pideSeleccionarResidenteYVisitante() {
        view.resident = Optional.empty();
        presenter.onGrant();
        assertTrue(view.error.contains("residente"));

        view.resident = Optional.of(view.residentOptions.get(0));
        view.visitor = Optional.empty();
        presenter.onGrant();
        assertTrue(view.error.contains("visitante"));
    }

    @Test
    void muestraErrorDeDominioSiElRangoEsInvalido() {
        view.until = view.from.minusHours(1);
        presenter.onGrant();
        assertEquals("La fecha final debe ser posterior a la inicial", view.error);
        assertTrue(view.rows.isEmpty());
    }

    @Test
    void revocaElPermisoSeleccionado() {
        presenter.onGrant();
        view.selected = Optional.of(view.rows.get(0));
        presenter.onRevoke();

        assertEquals("Permiso revocado", view.success);
        assertEquals(PermissionStatus.REVOKED, view.rows.get(0).status());
    }

    @Test
    void noRevocaSiElUsuarioCancela() {
        presenter.onGrant();
        view.selected = Optional.of(view.rows.get(0));
        view.confirm = false;
        presenter.onRevoke();

        assertEquals(PermissionStatus.ACTIVE, view.rows.get(0).status());
    }

    @Test
    void revocarDosVecesMuestraElErrorDelDominio() {
        presenter.onGrant();
        view.selected = Optional.of(view.rows.get(0));
        presenter.onRevoke();
        presenter.onRevoke();
        assertEquals("El permiso ya fue revocado", view.error);
    }

    @Test
    void revocarSinSeleccionPideSeleccionar() {
        presenter.onRevoke();
        assertEquals("Seleccione un permiso de la tabla", view.error);
        assertNull(view.success);
    }
}
