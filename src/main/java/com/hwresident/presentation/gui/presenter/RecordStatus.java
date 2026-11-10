package com.hwresident.presentation.gui.presenter;

/** Estado de un registro de la minuta, visto desde la GUI (espejo de AccessStatus sin depender del dominio). */
public enum RecordStatus {
    PENDING("En espera"),
    INSIDE("Dentro"),
    EXITED("Salida registrada"),
    REJECTED("Rechazado");

    private final String label;

    RecordStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
