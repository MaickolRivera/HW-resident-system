package com.hwresident.domain.model;

import java.util.UUID;

/** Persona que no vive en el conjunto: necesita autorización de un residente para entrar. */
public abstract class ExternalPerson extends Person {
    private static final long serialVersionUID = 1L;

    protected ExternalPerson(UUID id, String fullName, String documentNumber) {
        super(id, fullName, documentNumber);
    }
}
