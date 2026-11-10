package com.hwresident.domain.model;

import com.hwresident.domain.exception.DomainException;
import java.io.Serializable;

/** Vehículo con el que ingresa una persona (objeto de valor, opcional en el registro de acceso). */
public record Vehicle(String plate, VehicleType type) implements Serializable {
    public Vehicle {
        if (plate == null || plate.isBlank()) throw new DomainException("La placa es obligatoria");
        if (type == null) throw new DomainException("El tipo de vehículo es obligatorio");
        plate = plate.trim().toUpperCase();
    }

    public String label() {
        return type.label() + " " + plate;
    }
}
