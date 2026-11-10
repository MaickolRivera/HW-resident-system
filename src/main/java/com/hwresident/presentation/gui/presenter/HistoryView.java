package com.hwresident.presentation.gui.presenter;

import java.util.List;
import java.util.Optional;

/** Contrato de la pantalla "Historial" de accesos. */
public interface HistoryView {
    String documentFilter();
    Optional<ApartmentOption> apartmentFilter();
    boolean onlyToday();

    void showApartmentOptions(List<ApartmentOption> options);
    void showRecords(List<RecordRow> rows);
    void showSummary(String summary);
    void clearFilters();
}
