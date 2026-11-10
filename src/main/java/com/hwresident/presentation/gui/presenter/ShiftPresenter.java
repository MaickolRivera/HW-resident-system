package com.hwresident.presentation.gui.presenter;

import com.hwresident.application.usecase.ListGuardsUseCase;

public class ShiftPresenter {
    private final ShiftView view;
    private final ListGuardsUseCase guards;

    public ShiftPresenter(ShiftView view, ListGuardsUseCase guards) {
        this.view = view;
        this.guards = guards;
    }

    public void onShow() {
        view.showGuards(guards.execute().stream()
                .map(g -> new PersonOption(g.documentNumber(), g.displayName()))
                .toList());
    }
}
