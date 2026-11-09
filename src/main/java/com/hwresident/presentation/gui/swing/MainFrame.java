package com.hwresident.presentation.gui.swing;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.Dimension;

final class MainFrame extends JFrame {
    MainFrame(ResidentsPanel residents, VisitorsPanel visitors,
              PermissionsPanel permissions, AccessCheckPanel accessCheck) {
        super("HW Resident System");
        var tabs = new JTabbedPane();
        tabs.addTab("Residentes", residents);
        tabs.addTab("Visitantes", visitors);
        tabs.addTab("Permisos", permissions);
        tabs.addTab("Control de acceso", accessCheck);

        // Al cambiar de pestaña se recargan los datos (p. ej. nuevos residentes en los combos).
        tabs.addChangeListener(e -> {
            switch (tabs.getSelectedIndex()) {
                case 0 -> residents.onShow();
                case 1 -> visitors.onShow();
                case 2 -> permissions.onShow();
                default -> { }
            }
        });
        residents.onShow();

        setContentPane(tabs);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 520));
        pack();
        setLocationRelativeTo(null);
    }
}
