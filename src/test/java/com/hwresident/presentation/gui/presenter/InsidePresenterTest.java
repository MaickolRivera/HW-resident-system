package com.hwresident.presentation.gui.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hwresident.TestData;
import com.hwresident.application.usecase.EntryRequest;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InsidePresenterTest {

    private static class FakeView implements InsideView {
        boolean includeResidents;
        Optional<RecordRow> selected = Optional.empty();
        List<RecordRow> rows = List.of();
        String summary, success, error;

        public String guardDocument() { return "500"; }
        public boolean includeResidents() { return includeResidents; }
        public Optional<RecordRow> selectedRecord() { return selected; }
        public void showRecords(List<RecordRow> r) { rows = r; }
        public void showSummary(String s) { summary = s; }
        public void showSuccess(String m) { success = m; }
        public void showError(String m) { error = m; }
    }

    private TestData data;
    private FakeView view;
    private InsidePresenter presenter;

    @BeforeEach
    void setUp() {
        data = new TestData();
        var entry = data.registerEntry();
        entry.execute(EntryRequest.of("500", "123", null, null));
        var carlos = entry.execute(EntryRequest.of("500", "987", data.t1101.id(), null)).record();
        data.resolve().execute(carlos.id(), true);
        data.clock.advance(Duration.ofMinutes(75));

        view = new FakeView();
        presenter = new InsidePresenter(view, data.listInside(), data.registerExit(), data.clock);
    }

    @Test
    void muestraExternosDentroConTiempoTranscurrido() {
        presenter.onShow();
        assertEquals(1, view.rows.size());
        assertEquals("Carlos Ruiz", view.rows.get(0).person());
        assertEquals("1 h 15 min", view.rows.get(0).timeInside());
        assertEquals("1 persona(s) externa(s) dentro del conjunto", view.summary);
    }

    @Test
    void puedeIncluirResidentes() {
        view.includeResidents = true;
        presenter.onShow();
        assertEquals(2, view.rows.size());
    }

    @Test
    void darSalidaLoQuitaDeLaLista() {
        presenter.onShow();
        view.selected = Optional.of(view.rows.get(0));
        presenter.onExit();
        assertEquals("Salida registrada: Carlos Ruiz", view.success);
        assertTrue(view.rows.isEmpty());
    }
}
