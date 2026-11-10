package com.hwresident.presentation.gui.presenter;

import java.util.List;

/** Contrato de la pantalla de guardas. */
public interface GuardsView {
    String fullName();
    String documentNumber();

    void showGuards(List<GuardRow> rows);
    void clearForm();
    void showSuccess(String message);
    void showError(String message);
}
