package com.hwresident.presentation.gui.presenter;

import java.util.List;
import java.util.Optional;

/** Contrato que el presentador necesita de la pantalla de residentes. */
public interface ResidentsView {
    String fullName();
    String documentNumber();
    Optional<ApartmentOption> selectedApartment();

    void showApartmentOptions(List<ApartmentOption> options);
    void showResidents(List<ResidentRow> rows);
    void clearForm();
    void showSuccess(String message);
    void showError(String message);
}
