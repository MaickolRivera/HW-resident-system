package com.hwresident.domain.model;

import java.util.UUID;

/** Visitante particular (familiar, amigo...). */
public class Visitor extends ExternalPerson {
    private static final long serialVersionUID = 1L;

    public Visitor(UUID id, String fullName, String documentNumber) {
        super(id, fullName, documentNumber);
    }

    public static Visitor create(String fullName, String documentNumber) {
        return new Visitor(UUID.randomUUID(), fullName, documentNumber);
    }

    @Override
    public String typeLabel() { return "Visitante"; }
}
