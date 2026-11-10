package com.hwresident.presentation.gui.swing;

import com.hwresident.presentation.gui.presenter.ResidentRow;
import com.hwresident.presentation.gui.presenter.ResidentsPresenter;
import com.hwresident.presentation.gui.presenter.ResidentsView;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

/** Vista pasiva: solo lee/escribe widgets y delega todo al presentador. */
final class ResidentsPanel extends JPanel implements ResidentsView {
    private final JTextField name = new JTextField(24);
    private final JTextField document = new JTextField(24);
    private final JTextField unit = new JTextField(24);
    private final FeedbackLabel feedback = new FeedbackLabel();
    private final RowTableModel<ResidentRow> model = new RowTableModel<>(
            List.of("Nombre", "Documento", "Unidad"),
            List.of(ResidentRow::fullName, ResidentRow::documentNumber, ResidentRow::unit));
    private ResidentsPresenter presenter;

    ResidentsPanel() {
        super(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        var form = Ui.form();
        Ui.addRow(form, 0, "Nombre completo", name);
        Ui.addRow(form, 1, "Documento", document);
        Ui.addRow(form, 2, "Unidad", unit);
        var register = new JButton("Registrar residente");
        register.addActionListener(e -> presenter.onRegister());
        unit.addActionListener(e -> presenter.onRegister());

        var north = new JPanel(new BorderLayout(0, 8));
        north.add(form, BorderLayout.NORTH);
        var actions = new JPanel(new BorderLayout());
        actions.add(feedback, BorderLayout.CENTER);
        actions.add(register, BorderLayout.EAST);
        north.add(actions, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
    }

    void bind(ResidentsPresenter presenter) {
        this.presenter = presenter;
    }

    void onShow() {
        feedback.clear();
        presenter.onShow();
    }

    @Override public String fullName() { return name.getText(); }
    @Override public String documentNumber() { return document.getText(); }
    @Override public String unit() { return unit.getText(); }
    @Override public void showResidents(List<ResidentRow> rows) { model.setRows(rows); }

    @Override
    public void clearForm() {
        name.setText("");
        document.setText("");
        unit.setText("");
        name.requestFocusInWindow();
    }

    @Override public void showSuccess(String message) { feedback.success(message); }
    @Override public void showError(String message) { feedback.error(message); }
}
