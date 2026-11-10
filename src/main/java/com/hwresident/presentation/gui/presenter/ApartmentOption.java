package com.hwresident.presentation.gui.presenter;

import java.util.UUID;

/** Opción de un combo de apartamentos. id null = "Todos" (en filtros). */
public record ApartmentOption(UUID id, String label) {
    public static final ApartmentOption ALL = new ApartmentOption(null, "Todos");

    @Override
    public String toString() {
        return label;
    }
}
