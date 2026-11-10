package com.hwresident.presentation.gui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

final class MainFrame extends JFrame {

    /** Una pestaña y lo que hay que hacer cada vez que se muestra (recargar datos). */
    record Tab(String title, JComponent panel, Runnable onShow) {}

    MainFrame(ShiftBar shift, List<Tab> tabs) {
        super("HW Resident System - Portería");
        var tabbed = new JTabbedPane();
        tabs.forEach(t -> tabbed.addTab(t.title(), t.panel()));

        // Al cambiar de pestaña se recargan los datos (p. ej. nuevos residentes en los combos).
        tabbed.addChangeListener(e -> {
            shift.onShow();
            tabs.get(tabbed.getSelectedIndex()).onShow().run();
        });

        var title = new JLabel("Conjunto residencial - Minuta digital");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        var header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        header.add(title, BorderLayout.WEST);
        header.add(shift, BorderLayout.EAST);

        var content = new JPanel(new BorderLayout());
        content.add(header, BorderLayout.NORTH);
        content.add(tabbed, BorderLayout.CENTER);
        setContentPane(content);

        shift.onShow();
        tabs.get(0).onShow().run();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        pack();
        setLocationRelativeTo(null);
    }
}
