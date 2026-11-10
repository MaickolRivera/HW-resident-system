package com.hwresident.presentation.gui.presenter;

import com.hwresident.application.usecase.ListGuardsUseCase;
import com.hwresident.application.usecase.RegisterGuardUseCase;
import com.hwresident.domain.exception.DomainException;

public class GuardsPresenter {
    private final GuardsView view;
    private final RegisterGuardUseCase register;
    private final ListGuardsUseCase list;

    public GuardsPresenter(GuardsView view, RegisterGuardUseCase register, ListGuardsUseCase list) {
        this.view = view;
        this.register = register;
        this.list = list;
    }

    public void onShow() {
        view.showGuards(list.execute().stream()
                .map(g -> new GuardRow(g.fullName(), g.documentNumber()))
                .toList());
    }

    public void onRegister() {
        try {
            var guard = register.execute(view.fullName().trim(), view.documentNumber().trim());
            view.clearForm();
            onShow();
            view.showSuccess("Guarda registrado: " + guard.fullName());
        } catch (DomainException e) {
            view.showError(e.getMessage());
        }
    }
}
