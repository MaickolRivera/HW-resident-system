package com.hwresident.presentation.gui.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Helpers de layout compartidos por los paneles. */
final class Ui {
    private Ui() {}

    static JPanel form() {
        return new JPanel(new GridBagLayout());
    }

    /** Agrega una fila "etiqueta + campo" a un formulario GridBagLayout. */
    static void addRow(JPanel form, int row, String label, JComponent field) {
        var l = new GridBagConstraints();
        l.gridx = 0; l.gridy = row; l.anchor = GridBagConstraints.WEST; l.insets = new Insets(4, 0, 4, 12);
        form.add(new JLabel(label), l);
        var f = new GridBagConstraints();
        f.gridx = 1; f.gridy = row; f.weightx = 1; f.fill = GridBagConstraints.HORIZONTAL; f.insets = new Insets(4, 0, 4, 0);
        form.add(field, f);
    }
}
