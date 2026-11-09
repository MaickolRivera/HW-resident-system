package com.hwresident.presentation.gui.presenter;

import java.util.List;

/** Contrato que el presentador necesita de la pantalla de residentes. */
public interface ResidentsView {
    String fullName();
    String documentNumber();
    String unit();

    void showResidents(List<ResidentRow> rows);
    void clearForm();
    void showSuccess(String message);
    void showError(String message);
}
