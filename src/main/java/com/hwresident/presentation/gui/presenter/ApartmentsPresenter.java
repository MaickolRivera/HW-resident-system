package com.hwresident.presentation.gui.presenter;

import com.hwresident.application.usecase.ListApartmentsUseCase;
import com.hwresident.application.usecase.ListResidentsUseCase;
import com.hwresident.application.usecase.RegisterApartmentUseCase;
import com.hwresident.domain.exception.DomainException;
import com.hwresident.domain.model.Resident;
import java.util.stream.Collectors;

public class ApartmentsPresenter {
    private final ApartmentsView view;
    private final RegisterApartmentUseCase register;
    private final ListApartmentsUseCase list;
    private final ListResidentsUseCase residents;

    public ApartmentsPresenter(ApartmentsView view, RegisterApartmentUseCase register,
                               ListApartmentsUseCase list, ListResidentsUseCase residents) {
        this.view = view;
        this.register = register;
        this.list = list;
        this.residents = residents;
    }

    public void onShow() {
        var byApartment = residents.execute().stream()
                .collect(Collectors.groupingBy(r -> r.apartment().id(),
                        Collectors.mapping(Resident::fullName, Collectors.joining(", "))));
        view.showApartments(list.execute().stream()
                .map(a -> new ApartmentRow(a.tower(), a.number(), byApartment.getOrDefault(a.id(), "(sin residentes)")))
                .toList());
    }

    public void onRegister() {
        try {
            var apartment = register.execute(view.tower(), view.number());
            view.clearForm();
            onShow();
            view.showSuccess("Inmueble registrado: " + apartment.label());
        } catch (DomainException e) {
            view.showError(e.getMessage());
        }
    }
}
