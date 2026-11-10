package com.hwresident.presentation.gui.presenter;

import java.util.List;

/** Contrato de la pantalla de inmuebles. */
public interface ApartmentsView {
    String tower();
    String number();

    void showApartments(List<ApartmentRow> rows);
    void clearForm();
    void showSuccess(String message);
    void showError(String message);
}
