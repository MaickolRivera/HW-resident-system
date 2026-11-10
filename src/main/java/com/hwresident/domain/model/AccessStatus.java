package com.hwresident.domain.model;

/**
 * Estados de un registro de la minuta (máquina de estados):
 * EN_ESPERA -> DENTRO -> SALIDA_REGISTRADA, o bien EN_ESPERA -> RECHAZADO.
 */
public enum AccessStatus {
    PENDING("En espera"),
    INSIDE("Dentro"),
    EXITED("Salida registrada"),
    REJECTED("Rechazado");

    private final String label;

    AccessStatus(String label) {
        this.label = label;
    }

    public String label() { return label; }
}
