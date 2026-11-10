package com.hwresident.domain.model;

public enum VehicleType {
    CAR("Carro"),
    MOTORCYCLE("Moto"),
    BICYCLE("Bicicleta"),
    OTHER("Otro");

    private final String label;

    VehicleType(String label) {
        this.label = label;
    }

    public String label() { return label; }

    @Override
    public String toString() { return label; }
}
