package com.hwresident.presentation.gui.presenter;

import com.hwresident.application.usecase.ListResidentsUseCase;
import com.hwresident.application.usecase.RegisterResidentUseCase;
import com.hwresident.domain.exception.DomainException;

public class ResidentsPresenter {
    private final ResidentsView view;
    private final RegisterResidentUseCase register;
    private final ListResidentsUseCase list;

    public ResidentsPresenter(ResidentsView view, RegisterResidentUseCase register, ListResidentsUseCase list) {
        this.view = view;
        this.register = register;
        this.list = list;
    }

    public void onShow() {
        view.showResidents(list.execute().stream()
                .map(r -> new ResidentRow(r.fullName(), r.documentNumber(), r.unit()))
                .toList());
    }

    public void onRegister() {
        try {
            var resident = register.execute(view.fullName().trim(), view.documentNumber().trim(), view.unit().trim());
            view.clearForm();
            onShow();
            view.showSuccess("Residente registrado: " + resident.fullName());
        } catch (DomainException e) {
            view.showError(e.getMessage());
        }
    }
}
