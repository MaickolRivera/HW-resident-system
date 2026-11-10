package com.hwresident.presentation.gui.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hwresident.TestData;
import com.hwresident.application.usecase.CheckAccessUseCase;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccessCheckPresenterTest {

    private static final LocalDateTime FROM = LocalDateTime.of(2026, 11, 9, 10, 0);
    private static final LocalDateTime UNTIL = LocalDateTime.of(2026, 11, 9, 18, 0);

    private static class FakeView implements AccessCheckView {
        String document = "";
        LocalDateTime moment = FROM.plusHours(1);
        Boolean allowed;
        String message, error;

        public String visitorDocument() { return document; }
        public LocalDateTime moment() { return moment; }
        public void showAccessResult(boolean allowed, String message) { this.allowed = allowed; this.message = message; }
        public void showError(String message) { error = message; }
    }

    private FakeView view;
    private AccessCheckPresenter presenter;

    @BeforeEach
    void setUp() {
        var data = new TestData();
        data.grant().execute("123", "987", FROM, UNTIL);
        view = new FakeView();
        presenter = new AccessCheckPresenter(view, new CheckAccessUseCase(data.persons, data.permissions));
    }

    @Test
    void permiteAccesoDentroDeLaVentana() {
        view.document = " 987 ";
        presenter.onCheck();
        assertTrue(view.allowed);
        assertEquals("ACCESO PERMITIDO", view.message);
    }

    @Test
    void deniegaAccesoFueraDeLaVentana() {
        view.document = "987";
        view.moment = UNTIL.plusHours(1);
        presenter.onCheck();
        assertFalse(view.allowed);
    }

    @Test
    void deniegaVisitanteDesconocido() {
        view.document = "000";
        presenter.onCheck();
        assertFalse(view.allowed);
    }

    @Test
    void exigeDocumento() {
        view.document = "   ";
        presenter.onCheck();
        assertEquals("Ingrese el documento del visitante", view.error);
        assertNull(view.allowed);
    }
}
