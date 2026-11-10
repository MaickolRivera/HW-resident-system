package com.hwresident.domain.model;

import java.util.UUID;

/** Guarda de seguridad: es quien registra cada ingreso y salida en la minuta. */
public class Guard extends Person {
    private static final long serialVersionUID = 1L;

    public Guard(UUID id, String fullName, String documentNumber) {
        super(id, fullName, documentNumber);
    }

    public static Guard create(String fullName, String documentNumber) {
        return new Guard(UUID.randomUUID(), fullName, documentNumber);
    }

    @Override
    public String typeLabel() { return "Guarda"; }
}
