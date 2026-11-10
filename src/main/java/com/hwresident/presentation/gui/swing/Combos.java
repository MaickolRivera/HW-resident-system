package com.hwresident.presentation.gui.swing;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import javax.swing.JComboBox;

/** Utilidades para repoblar combos sin perder la selección. */
final class Combos {
    private Combos() {}

    /**
     * Repuebla el combo conservando la selección previa (comparando por clave).
     * Si no había selección: selectFirst=true elige la primera opción; false deja el combo sin selección.
     */
    static <T> void fill(JComboBox<T> combo, List<T> options, Function<T, Object> key, boolean selectFirst) {
        @SuppressWarnings("unchecked")
        var previous = (T) combo.getSelectedItem();
        combo.removeAllItems();
        options.forEach(combo::addItem);
        combo.setSelectedIndex(-1);
        if (previous != null) {
            options.stream().filter(o -> Objects.equals(key.apply(o), key.apply(previous)))
                    .findFirst().ifPresent(combo::setSelectedItem);
        }
        if (combo.getSelectedIndex() < 0 && selectFirst && !options.isEmpty()) {
            combo.setSelectedIndex(0);
        }
    }

    static void fillStrings(JComboBox<String> combo, List<String> options) {
        fill(combo, options, s -> s, true);
    }
}
