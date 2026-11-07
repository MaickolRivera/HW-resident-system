package com.hwresident.domain.model;

import com.hwresident.domain.exception.DomainException;
import java.util.UUID;

public class Visitor {
    private final UUID id;
    private final String fullName;
    private final String documentNumber;

    public Visitor(UUID id, String fullName, String documentNumber) {
        if (fullName == null || fullName.isBlank()) throw new DomainException("El nombre del visitante es obligatorio");
        if (documentNumber == null || !documentNumber.matches("\\d+")) {
            throw new DomainException("El documento del visitante debe ser numérico");
        }
        this.id = id;
        this.fullName = fullName.trim();
        this.documentNumber = documentNumber;
    }

    public static Visitor create(String fullName, String documentNumber) {
        return new Visitor(UUID.randomUUID(), fullName, documentNumber);
    }

    public UUID id() { return id; }
    public String fullName() { return fullName; }
    public String documentNumber() { return documentNumber; }
}
