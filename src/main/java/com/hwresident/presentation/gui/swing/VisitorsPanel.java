package com.hwresident.presentation.gui.swing;

import com.hwresident.presentation.gui.presenter.VisitorRow;
import com.hwresident.presentation.gui.presenter.VisitorsPresenter;
import com.hwresident.presentation.gui.presenter.VisitorsView;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

final class VisitorsPanel extends JPanel implements VisitorsView {
    private final JTextField name = new JTextField(24);
    private final JTextField document = new JTextField(24);
    private final FeedbackLabel feedback = new FeedbackLabel();
    private final RowTableModel<VisitorRow> model = new RowTableModel<>(
            List.of("Nombre", "Documento"),
            List.of(VisitorRow::fullName, VisitorRow::documentNumber));
    private VisitorsPresenter presenter;

    VisitorsPanel() {
        super(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        var form = Ui.form();
        Ui.addRow(form, 0, "Nombre completo", name);
        Ui.addRow(form, 1, "Documento", document);
        var register = new JButton("Registrar visitante");
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

    void bind(VisitorsPresenter presenter) {
        this.presenter = presenter;
    }

    void onShow() {
        feedback.clear();
        presenter.onShow();
    }

    @Override public String fullName() { return name.getText(); }
    @Override public String documentNumber() { return document.getText(); }
    @Override public void showVisitors(List<VisitorRow> rows) { model.setRows(rows); }

    @Override
    public void clearForm() {
        name.setText("");
        document.setText("");
        name.requestFocusInWindow();
    }

    @Override public void showSuccess(String message) { feedback.success(message); }
    @Override public void showError(String message) { feedback.error(message); }
}
