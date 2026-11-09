package com.hwresident.presentation.gui.presenter;

import com.hwresident.application.usecase.CheckAccessUseCase;

public class AccessCheckPresenter {
    private final AccessCheckView view;
    private final CheckAccessUseCase check;

    public AccessCheckPresenter(AccessCheckView view, CheckAccessUseCase check) {
        this.view = view;
        this.check = check;
    }

    public void onCheck() {
        var document = view.visitorDocument().trim();
        if (document.isEmpty()) {
            view.showError("Ingrese el documento del visitante");
            return;
        }
        if (check.execute(document, view.moment())) {
            view.showAccessResult(true, "ACCESO PERMITIDO");
        } else {
            // El caso de uso no distingue "no registrado" de "sin permiso vigente".
            view.showAccessResult(false, "ACCESO DENEGADO - sin permiso vigente o visitante no registrado");
        }
    }
}
