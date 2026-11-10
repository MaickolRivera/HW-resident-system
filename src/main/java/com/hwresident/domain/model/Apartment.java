package com.hwresident.domain.model;

import com.hwresident.domain.exception.DomainException;
import java.io.Serializable;
import java.util.UUID;

/** Inmueble del conjunto, identificado por torre y número. */
public class Apartment implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final String tower;
    private final String number;

    public Apartment(UUID id, String tower, String number) {
        if (tower == null || tower.isBlank()) throw new DomainException("La torre es obligatoria");
        if (number == null || number.isBlank()) throw new DomainException("El número de apartamento es obligatorio");
        this.id = id;
        this.tower = tower.trim();
        this.number = number.trim();
    }

    public static Apartment create(String tower, String number) {
        return new Apartment(UUID.randomUUID(), tower, number);
    }

    public boolean isSameUnit(String otherTower, String otherNumber) {
        return tower.equalsIgnoreCase(otherTower.trim()) && number.equalsIgnoreCase(otherNumber.trim());
    }

    public UUID id() { return id; }
    public String tower() { return tower; }
    public String number() { return number; }

    /** Ej.: "Torre 1 - 101". */
    public String label() {
        return "Torre " + tower + " - " + number;
    }
}
