package com.hwresident.presentation.gui.presenter;

import com.hwresident.application.usecase.ListPeopleInsideUseCase;
import com.hwresident.application.usecase.RegisterExitUseCase;
import com.hwresident.domain.exception.DomainException;
import java.time.Clock;
import java.time.LocalDateTime;

/** "Personas dentro del conjunto": ingresos sin salida registrada, en tiempo real. */
public class InsidePresenter {
    private final InsideView view;
    private final ListPeopleInsideUseCase listInside;
    private final RegisterExitUseCase registerExit;
    private final Clock clock;

    public InsidePresenter(InsideView view, ListPeopleInsideUseCase listInside,
                           RegisterExitUseCase registerExit, Clock clock) {
        this.view = view;
        this.listInside = listInside;
        this.registerExit = registerExit;
        this.clock = clock;
    }

    public void onShow() {
        var now = LocalDateTime.now(clock);
        var rows = listInside.execute(view.includeResidents()).stream()
                .map(r -> RecordRow.of(r, now))
                .toList();
        view.showRecords(rows);
        var who = view.includeResidents() ? " persona(s)" : " persona(s) externa(s)";
        view.showSummary(rows.size() + who + " dentro del conjunto");
    }

    public void onExit() {
        var selected = view.selectedRecord();
        if (selected.isEmpty()) {
            view.showError("Seleccione una persona de la tabla");
            return;
        }
        try {
            var record = registerExit.execute(selected.get().id(), view.guardDocument());
            onShow();
            view.showSuccess("Salida registrada: " + record.person().fullName());
        } catch (DomainException e) {
            view.showError(e.getMessage());
        }
    }
}
