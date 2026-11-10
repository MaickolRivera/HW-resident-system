package com.hwresident.presentation.gui.swing;

import com.hwresident.presentation.gui.presenter.PersonOption;
import com.hwresident.presentation.gui.presenter.ShiftPresenter;
import com.hwresident.presentation.gui.presenter.ShiftView;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Barra superior con el guarda de turno: quien queda registrado en cada ingreso y salida. */
final class ShiftBar extends JPanel implements ShiftView {
    private final JComboBox<PersonOption> guard = new JComboBox<>();
    private ShiftPresenter presenter;

    ShiftBar() {
        super(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        var label = new JLabel("Guarda de turno:");
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        add(label);
        add(guard);
    }

    void bind(ShiftPresenter presenter) {
        this.presenter = presenter;
    }

    void onShow() {
        presenter.onShow();
    }

    String guardDocument() {
        var selected = (PersonOption) guard.getSelectedItem();
        return selected == null ? "" : selected.documentNumber();
    }

    @Override
    public void showGuards(List<PersonOption> guards) {
        Combos.fill(guard, guards, PersonOption::documentNumber, true);
    }
}
