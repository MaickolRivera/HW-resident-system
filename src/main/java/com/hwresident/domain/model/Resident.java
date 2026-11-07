package com.hwresident.domain.model;

import com.hwresident.domain.exception.DomainException;
import java.util.UUID;

/** Residente de un conjunto. Entidad de dominio sin dependencias externas. */
public class Resident {
    private final UUID id;
    private final String fullName;
    private final String documentNumber;
    private final String unit;

    public Resident(UUID id, String fullName, String documentNumber, String unit) {
        if (fullName == null || fullName.isBlank()) throw new DomainException("El nombre es obligatorio");
        if (documentNumber == null || !documentNumber.matches("\\d+")) {
            throw new DomainException("El documento debe ser numérico");
        }
        if (unit == null || unit.isBlank()) throw new DomainException("La unidad es obligatoria");
        this.id = id;
        this.fullName = fullName.trim();
        this.documentNumber = documentNumber;
        this.unit = unit.trim();
    }

    public static Resident create(String fullName, String documentNumber, String unit) {
        return new Resident(UUID.randomUUID(), fullName, documentNumber, unit);
    }

    public UUID id() { return id; }
    public String fullName() { return fullName; }
    public String documentNumber() { return documentNumber; }
    public String unit() { return unit; }
}
