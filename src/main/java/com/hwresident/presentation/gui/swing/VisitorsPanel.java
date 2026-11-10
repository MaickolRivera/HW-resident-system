package com.hwresident.presentation.gui.swing;

import com.hwresident.presentation.gui.presenter.VisitorRow;
import com.hwresident.presentation.gui.presenter.VisitorsPresenter;
import com.hwresident.presentation.gui.presenter.VisitorsView;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

/** Pantalla "Externos": visitantes, domiciliarios, contratistas y personal de servicio. */
final class VisitorsPanel extends JPanel implements VisitorsView {
    private final JTextField name = new JTextField(24);
    private final JTextField document = new JTextField(24);
    private final JComboBox<String> type = new JComboBox<>();
    private final JTextField company = new JTextField(24);
    private final JTextField labor = new JTextField(24);
    private final FeedbackLabel feedback = new FeedbackLabel();
    private final RowTableModel<VisitorRow> model = new RowTableModel<>(
            List.of("Nombre", "Documento", "Tipo", "Empresa", "Labor"),
            List.of(VisitorRow::fullName, VisitorRow::documentNumber, VisitorRow::type,
                    VisitorRow::company, VisitorRow::labor));
    private VisitorsPresenter presenter;

    VisitorsPanel() {
        super(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        var form = Ui.form();
        Ui.addRow(form, 0, "Nombre completo", name);
        Ui.addRow(form, 1, "Documento", document);
        Ui.addRow(form, 2, "Tipo", type);
        Ui.addRow(form, 3, "Empresa", company);
        Ui.addRow(form, 4, "Labor", labor);
        type.addActionListener(e -> updateServiceFields());
        var register = new JButton("Registrar");
        register.addActionListener(e -> presenter.onRegister());

        var north = new JPanel(new BorderLayout(0, 8));
        north.add(form, BorderLayout.NORTH);
        var actions = new JPanel(new BorderLayout());
        actions.add(feedback, BorderLayout.CENTER);
        actions.add(register, BorderLayout.EAST);
        north.add(actions, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        updateServiceFields();
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
    @Override public String personType() { return (String) type.getSelectedItem(); }
    @Override public String company() { return company.getText(); }
    @Override public String labor() { return labor.getText(); }
    @Override public void showPersonTypes(List<String> types) { Combos.fillStrings(type, types); }
    @Override public void showVisitors(List<VisitorRow> rows) { model.setRows(rows); }

    @Override
    public void clearForm() {
        name.setText("");
        document.setText("");
        company.setText("");
        labor.setText("");
        name.requestFocusInWindow();
    }

    @Override public void showSuccess(String message) { feedback.success(message); }
    @Override public void showError(String message) { feedback.error(message); }

    /** Empresa y labor solo aplican a prestadores de servicio (el primer tipo es "Visitante"). */
    private void updateServiceFields() {
        boolean provider = type.getSelectedIndex() > 0;
        company.setEnabled(provider);
        labor.setEnabled(provider);
    }
}
