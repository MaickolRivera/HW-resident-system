package com.hwresident.presentation.gui.presenter;

public enum PermissionStatus {
    ACTIVE("Vigente"),
    SCHEDULED("Programado"),
    EXPIRED("Vencido"),
    REVOKED("Revocado");

    private final String label;

    PermissionStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
