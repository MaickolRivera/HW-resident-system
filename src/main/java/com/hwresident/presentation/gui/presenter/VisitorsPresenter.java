package com.hwresident.presentation.gui.presenter;

import com.hwresident.application.usecase.ListVisitorsUseCase;
import com.hwresident.application.usecase.RegisterVisitorUseCase;
import com.hwresident.domain.exception.DomainException;

public class VisitorsPresenter {
    private final VisitorsView view;
    private final RegisterVisitorUseCase register;
    private final ListVisitorsUseCase list;

    public VisitorsPresenter(VisitorsView view, RegisterVisitorUseCase register, ListVisitorsUseCase list) {
        this.view = view;
        this.register = register;
        this.list = list;
    }

    public void onShow() {
        view.showVisitors(list.execute().stream()
                .map(v -> new VisitorRow(v.fullName(), v.documentNumber()))
                .toList());
    }

    public void onRegister() {
        try {
            var visitor = register.execute(view.fullName().trim(), view.documentNumber().trim());
            view.clearForm();
            onShow();
            view.showSuccess("Visitante registrado: " + visitor.fullName());
        } catch (DomainException e) {
            view.showError(e.getMessage());
        }
    }
}
