package com.hwresident.presentation.gui.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hwresident.TestData;
import com.hwresident.application.usecase.FindPersonUseCase;
import com.hwresident.application.usecase.ListApartmentsUseCase;
import com.hwresident.application.usecase.ListResidentsUseCase;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogbookPresenterTest {

    private static class FakeView implements LogbookView {
        String guard = "500", document = "", name = "", type = "Visitante", company = "", labor = "";
        Optional<ApartmentOption> destination = Optional.empty();
        String reason = "", plate = "", vehicleType = "Carro", correspondence = "";
        Optional<RecordRow> selected = Optional.empty();
        List<ApartmentOption> apartmentOptions = List.of();
        List<String> personTypes = List.of();
        List<RecordRow> rows = List.of();
        String lookup, success, error, question;
        Boolean registered;
        UUID fixedDestination;
        boolean destinationEditable = true;
        Optional<Boolean> answer = Optional.empty();

        public String guardDocument() { return guard; }
        public String documentNumber() { return document; }
        public String fullName() { return name; }
        public String personType() { return type; }
        public String company() { return company; }
        public String labor() { return labor; }
        public Optional<ApartmentOption> selectedDestination() { return destination; }
        public String reason() { return reason; }
        public String vehiclePlate() { return plate; }
        public String vehicleType() { return vehicleType; }
        public String correspondence() { return correspondence; }
        public Optional<RecordRow> selectedRecord() { return selected; }
        public void showApartmentOptions(List<ApartmentOption> o) { apartmentOptions = o; }
        public void showPersonTypes(List<String> t) { personTypes = t; }
        public void showVehicleTypes(List<String> t) { }
        public void showPersonLookup(String m, boolean r) { lookup = m; registered = r; }
        public void setDestination(UUID id, boolean editable) { fixedDestination = id; destinationEditable = editable; }
        public void showRecords(List<RecordRow> r) { rows = r; }
        public Optional<Boolean> askResidentAuthorization(String q) { question = q; return answer; }
        public void clearForm() { document = ""; name = ""; }
        public void showSuccess(String m) { success = m; error = null; }
        public void showError(String m) { error = m; success = null; }
    }

    private TestData data;
    private FakeView view;
    private LogbookPresenter presenter;

    @BeforeEach
    void setUp() {
        data = new TestData();
        view = new FakeView();
        presenter = new LogbookPresenter(view, new FindPersonUseCase(data.persons),
                new ListApartmentsUseCase(data.apartments), new ListResidentsUseCase(data.persons),
                data.registerEntry(), data.resolve(), data.registerExit(), data.history(), data.clock);
        presenter.onShow();
    }

    private ApartmentOption option(String label) {
        return view.apartmentOptions.stream().filter(o -> o.label().equals(label)).findFirst().orElseThrow();
    }

    @Test
    void ofreceTiposDePersonaYApartamentos() {
        assertEquals(List.of("Visitante", "Domiciliario", "Contratista", "Personal de servicio"), view.personTypes);
        assertEquals(2, view.apartmentOptions.size());
    }

    @Test
    void buscarResidenteFijaSuApartamento() {
        view.document = "123";
        presenter.onLookup();
        assertTrue(view.registered);
        assertTrue(view.lookup.startsWith("Residente: Ana Pérez"));
        assertEquals(data.t1101.id(), view.fixedDestination);
        assertFalse(view.destinationEditable);
    }

    @Test
    void buscarDocumentoDesconocidoPideDatos() {
        view.document = "444";
        presenter.onLookup();
        assertFalse(view.registered);
        assertTrue(view.destinationEditable);
    }

    @Test
    void residenteEntraSinPreguntar() {
        view.document = "123";
        presenter.onRegisterEntry();

        assertNull(view.question);
        assertTrue(view.success.startsWith("Ingreso registrado: Ana Pérez"));
        assertEquals(RecordStatus.INSIDE, view.rows.get(0).status());
    }

    @Test
    void externoSinPermisoPreguntaAlResidenteYQuedaDentroSiAutoriza() {
        view.document = "987";
        view.destination = Optional.of(option("Torre 1 - 101"));
        view.answer = Optional.of(true);
        presenter.onRegisterEntry();

        assertNotNull(view.question);
        assertTrue(view.question.contains("Residentes: Ana Pérez"));
        assertTrue(view.success.startsWith("Ingreso autorizado"));
        assertEquals(RecordStatus.INSIDE, view.rows.get(0).status());
    }

    @Test
    void siElResidenteNoAutorizaQuedaRechazado() {
        view.document = "987";
        view.destination = Optional.of(option("Torre 1 - 101"));
        view.answer = Optional.of(false);
        presenter.onRegisterEntry();

        assertTrue(view.error.contains("RECHAZADO"));
        assertEquals(RecordStatus.REJECTED, view.rows.get(0).status());
    }

    @Test
    void decidirDespuesDejaEnEsperaYSeAutorizaDesdeLaTabla() {
        view.document = "987";
        view.destination = Optional.of(option("Torre 1 - 101"));
        presenter.onRegisterEntry();
        assertEquals(RecordStatus.PENDING, view.rows.get(0).status());

        view.selected = Optional.of(view.rows.get(0));
        presenter.onAuthorize();
        assertEquals(RecordStatus.INSIDE, view.rows.get(0).status());

        presenter.onExit();
        assertEquals("Salida registrada: Carlos Ruiz", view.success);
        assertEquals(RecordStatus.EXITED, view.rows.get(0).status());
    }

    @Test
    void registraPersonaNuevaComoPrestador() {
        view.document = "444";
        view.name = "Felipe Rojas";
        view.type = "Contratista";
        view.company = "Pinturas JR";
        view.labor = "Pintura";
        view.plate = "abc123";
        view.destination = Optional.of(option("Torre 2 - 302"));
        view.answer = Optional.of(true);
        presenter.onRegisterEntry();

        var row = view.rows.get(0);
        assertEquals("Contratista", row.type());
        assertEquals("Carro ABC123", row.vehicle());
        assertEquals("Entrada: Pedro Castillo", row.guards());
    }

    @Test
    void erroresDelDominioSeMuestranEnLaVista() {
        view.document = "123";
        presenter.onRegisterEntry();
        view.document = "123";
        presenter.onRegisterEntry();
        assertTrue(view.error.contains("ya se encuentra dentro"));

        view.selected = Optional.empty();
        presenter.onExit();
        assertEquals("Seleccione un registro de la tabla", view.error);
    }
}
