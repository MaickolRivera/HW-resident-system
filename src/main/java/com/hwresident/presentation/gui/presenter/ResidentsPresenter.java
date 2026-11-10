package com.hwresident.presentation.gui.presenter;

import com.hwresident.application.usecase.ListApartmentsUseCase;
import com.hwresident.application.usecase.ListResidentsUseCase;
import com.hwresident.application.usecase.RegisterResidentUseCase;
import com.hwresident.domain.exception.DomainException;
import java.util.Comparator;

public class ResidentsPresenter {
    private final ResidentsView view;
    private final RegisterResidentUseCase register;
    private final ListResidentsUseCase list;
    private final ListApartmentsUseCase apartments;

    public ResidentsPresenter(ResidentsView view, RegisterResidentUseCase register,
                              ListResidentsUseCase list, ListApartmentsUseCase apartments) {
        this.view = view;
        this.register = register;
        this.list = list;
        this.apartments = apartments;
    }

    public void onShow() {
        view.showApartmentOptions(apartments.execute().stream().map(Labels::option).toList());
        refreshTable();
    }

    public void onRegister() {
        var apartment = view.selectedApartment();
        if (apartment.isEmpty()) {
            view.showError("Seleccione un apartamento (regístrelo primero en Inmuebles)");
            return;
        }
        try {
            var resident = register.execute(view.fullName().trim(), view.documentNumber().trim(), apartment.get().id());
            view.clearForm();
            refreshTable();
            view.showSuccess("Residente registrado: " + resident.fullName());
        } catch (DomainException e) {
            view.showError(e.getMessage());
        }
    }

    private void refreshTable() {
        view.showResidents(list.execute().stream()
                .map(r -> new ResidentRow(r.fullName(), r.documentNumber(), r.apartment().label()))
                .sorted(Comparator.comparing(ResidentRow::apartment).thenComparing(ResidentRow::fullName))
                .toList());
    }
}
