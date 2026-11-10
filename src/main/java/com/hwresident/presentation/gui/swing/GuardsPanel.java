package com.hwresident.presentation.gui.swing;

import com.hwresident.presentation.gui.presenter.GuardRow;
import com.hwresident.presentation.gui.presenter.GuardsPresenter;
import com.hwresident.presentation.gui.presenter.GuardsView;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

final class GuardsPanel extends JPanel implements GuardsView {
    private final JTextField name = new JTextField(24);
    private final JTextField document = new JTextField(24);
    private final FeedbackLabel feedback = new FeedbackLabel();
    private final RowTableModel<GuardRow> model = new RowTableModel<>(
            List.of("Nombre", "Documento"),
            List.of(GuardRow::fullName, GuardRow::documentNumber));
    private GuardsPresenter presenter;

    GuardsPanel() {
        super(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        var form = Ui.form();
        Ui.addRow(form, 0, "Nombre completo", name);
        Ui.addRow(form, 1, "Documento", document);
        var register = new JButton("Registrar guarda");
        register.addActionListener(e -> presenter.onRegister());
        document.addActionListener(e -> presenter.onRegister());

        var north = new JPanel(new BorderLayout(0, 8));
        north.add(form, BorderLayout.NORTH);
        var actions = new JPanel(new BorderLayout());
        actions.add(feedback, BorderLayout.CENTER);
        actions.add(register, BorderLayout.EAST);
        north.add(actions, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
    }

    void bind(GuardsPresenter presenter) {
        this.presenter = presenter;
    }

    void onShow() {
        feedback.clear();
        presenter.onShow();
    }

    @Override public String fullName() { return name.getText(); }
    @Override public String documentNumber() { return document.getText(); }
    @Override public void showGuards(List<GuardRow> rows) { model.setRows(rows); }

    @Override
    public void clearForm() {
        name.setText("");
        document.setText("");
        name.requestFocusInWindow();
    }

    @Override public void showSuccess(String message) { feedback.success(message); }
    @Override public void showError(String message) { feedback.error(message); }
}
