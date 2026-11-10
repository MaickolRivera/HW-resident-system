package com.hwresident.presentation.gui.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hwresident.application.usecase.ListApartmentsUseCase;
import com.hwresident.application.usecase.ListResidentsUseCase;
import com.hwresident.application.usecase.RegisterApartmentUseCase;
import com.hwresident.application.usecase.RegisterResidentUseCase;
import com.hwresident.infrastructure.persistence.InMemoryApartmentRepository;
import com.hwresident.infrastructure.persistence.InMemoryPersonRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResidentsPresenterTest {

    private static class FakeView implements ResidentsView {
        String name = "", document = "";
        Optional<ApartmentOption> apartment = Optional.empty();
        List<ApartmentOption> apartmentOptions = List.of();
        List<ResidentRow> rows = List.of();
        String success, error;
        boolean cleared;

        public String fullName() { return name; }
        public String documentNumber() { return document; }
        public Optional<ApartmentOption> selectedApartment() { return apartment; }
        public void showApartmentOptions(List<ApartmentOption> options) { apartmentOptions = options; }
        public void showResidents(List<ResidentRow> rows) { this.rows = rows; }
        public void clearForm() { cleared = true; }
        public void showSuccess(String message) { success = message; }
        public void showError(String message) { error = message; }
    }

    private FakeView view;
    private ResidentsPresenter presenter;

    @BeforeEach
    void setUp() {
        var persons = new InMemoryPersonRepository();
        var apartments = new InMemoryApartmentRepository();
        new RegisterApartmentUseCase(apartments).execute("1", "101");
        view = new FakeView();
        presenter = new ResidentsPresenter(view, new RegisterResidentUseCase(persons, apartments),
                new ListResidentsUseCase(persons), new ListApartmentsUseCase(apartments));
        presenter.onShow();
        view.apartment = Optional.of(view.apartmentOptions.get(0));
    }

    @Test
    void cargaLosApartamentos() {
        assertEquals("Torre 1 - 101", view.apartmentOptions.get(0).label());
    }

    @Test
    void registraYActualizaLaTabla() {
        view.name = "  Ana Pérez ";
        view.document = " 123 ";
        presenter.onRegister();

        assertNull(view.error);
        assertTrue(view.cleared);
        assertEquals(List.of(new ResidentRow("Ana Pérez", "123", "Torre 1 - 101")), view.rows);
        assertEquals("Residente registrado: Ana Pérez", view.success);
    }

    @Test
    void exigeApartamento() {
        view.name = "Ana";
        view.document = "123";
        view.apartment = Optional.empty();
        presenter.onRegister();
        assertTrue(view.error.startsWith("Seleccione un apartamento"));
    }

    @Test
    void muestraElErrorDeDominioSinRomperse() {
        view.name = "Ana";
        view.document = "abc";
        presenter.onRegister();

        assertEquals("El documento debe ser numérico", view.error);
        assertNull(view.success);
    }

    @Test
    void rechazaDocumentoDuplicado() {
        view.name = "Ana"; view.document = "123";
        presenter.onRegister();
        presenter.onRegister();
        assertEquals("Ya existe una persona con ese documento (residente: Ana)", view.error);
        assertEquals(1, view.rows.size());
    }
}
