package com.hwresident.presentation.gui.presenter;

import java.util.List;

/** Contrato que el presentador necesita de la pantalla de visitantes. */
public interface VisitorsView {
    String fullName();
    String documentNumber();

    void showVisitors(List<VisitorRow> rows);
    void clearForm();
    void showSuccess(String message);
    void showError(String message);
}
