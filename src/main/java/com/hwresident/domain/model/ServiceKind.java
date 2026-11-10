package com.hwresident.domain.model;

/** Tipos de prestador de servicio que ingresan al conjunto. */
public enum ServiceKind {
    DELIVERY("Domiciliario"),
    CONTRACTOR("Contratista"),
    SERVICE_STAFF("Personal de servicio");

    private final String label;

    ServiceKind(String label) {
        this.label = label;
    }

    public String label() { return label; }

    @Override
    public String toString() { return label; }
}
