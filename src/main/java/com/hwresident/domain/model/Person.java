package com.hwresident.domain.model;

import com.hwresident.domain.exception.DomainException;
import java.io.Serializable;
import java.util.UUID;

/**
 * Superclase de todos los actores del conjunto (residentes, externos y guardas).
 * Concentra la identidad y las validaciones comunes: nombre y documento numérico.
 */
public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final String fullName;
    private final String documentNumber;

    protected Person(UUID id, String fullName, String documentNumber) {
        if (id == null) throw new DomainException("El identificador es obligatorio");
        if (fullName == null || fullName.isBlank()) throw new DomainException("El nombre es obligatorio");
        if (documentNumber == null || !documentNumber.trim().matches("\\d+")) {
            throw new DomainException("El documento debe ser numérico");
        }
        this.id = id;
        this.fullName = fullName.trim();
        this.documentNumber = documentNumber.trim();
    }

    /** Nombre del tipo de actor para mostrar (Residente, Visitante, Domiciliario...). */
    public abstract String typeLabel();

    public UUID id() { return id; }
    public String fullName() { return fullName; }
    public String documentNumber() { return documentNumber; }

    /** Ej.: "Ana Pérez (123)". */
    public String displayName() {
        return fullName + " (" + documentNumber + ")";
    }
}
