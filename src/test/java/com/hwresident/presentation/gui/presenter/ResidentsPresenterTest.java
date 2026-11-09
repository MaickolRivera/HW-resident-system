package com.hwresident.presentation.gui.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hwresident.application.usecase.ListResidentsUseCase;
import com.hwresident.application.usecase.RegisterResidentUseCase;
import com.hwresident.infrastructure.persistence.InMemoryResidentRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResidentsPresenterTest {

    private static class FakeView implements ResidentsView {
        String name = "", document = "", unit = "";
        List<ResidentRow> rows = List.of();
        String success, error;
        boolean cleared;

        public String fullName() { return name; }
        public String documentNumber() { return document; }
        public String unit() { return unit; }
        public void showResidents(List<ResidentRow> rows) { this.rows = rows; }
        public void clearForm() { cleared = true; }
        public void showSuccess(String message) { success = message; }
        public void showError(String message) { error = message; }
    }

    private FakeView view;
    private ResidentsPresenter presenter;

    @BeforeEach
    void setUp() {
        var repo = new InMemoryResidentRepository();
        view = new FakeView();
        presenter = new ResidentsPresenter(view, new RegisterResidentUseCase(repo), new ListResidentsUseCase(repo));
    }

    @Test
    void registraYActualizaLaTabla() {
        view.name = "  Ana Pérez ";
        view.document = " 123 ";
        view.unit = "Torre 1 - 101";
        presenter.onRegister();

        assertNull(view.error);
        assertTrue(view.cleared);
        assertEquals(List.of(new ResidentRow("Ana Pérez", "123", "Torre 1 - 101")), view.rows);
        assertEquals("Residente registrado: Ana Pérez", view.success);
    }

    @Test
    void muestraElErrorDeDominioSinRomperse() {
        view.name = "Ana";
        view.document = "abc";
        view.unit = "101";
        presenter.onRegister();

        assertEquals("El documento debe ser numérico", view.error);
        assertNull(view.success);
    }

    @Test
    void rechazaDocumentoDuplicado() {
        view.name = "Ana"; view.document = "123"; view.unit = "101";
        presenter.onRegister();
        presenter.onRegister();
        assertEquals("Ya existe un residente con ese documento", view.error);
        assertEquals(1, view.rows.size());
    }
}
