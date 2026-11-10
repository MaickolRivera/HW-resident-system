package com.hwresident.presentation.gui.presenter;

import java.util.List;

/** Contrato de la pantalla de personas externas (visitantes, domiciliarios, contratistas, personal de servicio). */
public interface VisitorsView {
    String fullName();
    String documentNumber();
    String personType();
    String company();
    String labor();

    void showPersonTypes(List<String> types);
    void showVisitors(List<VisitorRow> rows);
    void clearForm();
    void showSuccess(String message);
    void showError(String message);
}
