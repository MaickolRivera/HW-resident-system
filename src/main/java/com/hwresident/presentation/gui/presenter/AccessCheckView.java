package com.hwresident.presentation.gui.presenter;

import java.time.LocalDateTime;

/** Contrato que el presentador necesita de la pantalla de control de acceso. */
public interface AccessCheckView {
    String visitorDocument();
    LocalDateTime moment();

    void showAccessResult(boolean allowed, String message);
    void showError(String message);
}
