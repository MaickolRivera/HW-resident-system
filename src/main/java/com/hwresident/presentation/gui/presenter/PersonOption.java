package com.hwresident.presentation.gui.presenter;

/** Opción de un combo: etiqueta legible + documento (clave que usan los casos de uso). */
public record PersonOption(String documentNumber, String label) {
    @Override
    public String toString() {
        return label;
    }
}
