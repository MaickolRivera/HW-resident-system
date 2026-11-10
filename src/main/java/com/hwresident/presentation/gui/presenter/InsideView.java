package com.hwresident.presentation.gui.presenter;

import java.util.List;
import java.util.Optional;

/** Contrato de la pantalla "Dentro del conjunto". */
public interface InsideView {
    String guardDocument();
    boolean includeResidents();
    Optional<RecordRow> selectedRecord();

    void showRecords(List<RecordRow> rows);
    void showSummary(String summary);
    void showSuccess(String message);
    void showError(String message);
}
