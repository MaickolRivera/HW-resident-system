package com.hwresident.presentation.gui.presenter;

import com.hwresident.application.usecase.ListExternalPersonsUseCase;
import com.hwresident.application.usecase.RegisterServiceProviderUseCase;
import com.hwresident.application.usecase.RegisterVisitorUseCase;
import com.hwresident.domain.exception.DomainException;
import com.hwresident.domain.model.ExternalPerson;
import com.hwresident.domain.model.ServiceProvider;

/** Pantalla "Externos": visitantes y prestadores de servicio. */
public class VisitorsPresenter {
    private final VisitorsView view;
    private final RegisterVisitorUseCase registerVisitor;
    private final RegisterServiceProviderUseCase registerProvider;
    private final ListExternalPersonsUseCase list;

    public VisitorsPresenter(VisitorsView view, RegisterVisitorUseCase registerVisitor,
                             RegisterServiceProviderUseCase registerProvider, ListExternalPersonsUseCase list) {
        this.view = view;
        this.registerVisitor = registerVisitor;
        this.registerProvider = registerProvider;
        this.list = list;
        view.showPersonTypes(Labels.externalTypes());
    }

    public void onShow() {
        view.showVisitors(list.execute().stream().map(VisitorsPresenter::rowOf).toList());
    }

    public void onRegister() {
        try {
            var kind = Labels.serviceKindOf(view.personType());
            ExternalPerson person = kind == null
                    ? registerVisitor.execute(view.fullName().trim(), view.documentNumber().trim())
                    : registerProvider.execute(view.fullName().trim(), view.documentNumber().trim(),
                            kind, view.company(), view.labor());
            view.clearForm();
            onShow();
            view.showSuccess(person.typeLabel() + " registrado: " + person.fullName());
        } catch (DomainException e) {
            view.showError(e.getMessage());
        }
    }

    private static VisitorRow rowOf(ExternalPerson person) {
        if (person instanceof ServiceProvider p) {
            return new VisitorRow(p.fullName(), p.documentNumber(), p.typeLabel(), p.company(), p.labor());
        }
        return new VisitorRow(person.fullName(), person.documentNumber(), person.typeLabel(), "", "");
    }
}
