package com.hwresident.presentation.gui.swing;

import com.hwresident.presentation.gui.presenter.ApartmentRow;
import com.hwresident.presentation.gui.presenter.ApartmentsPresenter;
import com.hwresident.presentation.gui.presenter.ApartmentsView;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

final class ApartmentsPanel extends JPanel implements ApartmentsView {
    private final JTextField tower = new JTextField(24);
    private final JTextField number = new JTextField(24);
    private final FeedbackLabel feedback = new FeedbackLabel();
    private final RowTableModel<ApartmentRow> model = new RowTableModel<>(
            List.of("Torre", "Número", "Residentes"),
            List.of(ApartmentRow::tower, ApartmentRow::number, ApartmentRow::residents));
    private ApartmentsPresenter presenter;

    ApartmentsPanel() {
        super(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        var form = Ui.form();
        Ui.addRow(form, 0, "Torre", tower);
        Ui.addRow(form, 1, "Número", number);
        var register = new JButton("Registrar inmueble");
        register.addActionListener(e -> presenter.onRegister());
        number.addActionListener(e -> presenter.onRegister());

        var north = new JPanel(new BorderLayout(0, 8));
        north.add(form, BorderLayout.NORTH);
        var actions = new JPanel(new BorderLayout());
        actions.add(feedback, BorderLayout.CENTER);
        actions.add(register, BorderLayout.EAST);
        north.add(actions, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
    }

    void bind(ApartmentsPresenter presenter) {
        this.presenter = presenter;
    }

    void onShow() {
        feedback.clear();
        presenter.onShow();
    }

    @Override public String tower() { return tower.getText(); }
    @Override public String number() { return number.getText(); }
    @Override public void showApartments(List<ApartmentRow> rows) { model.setRows(rows); }

    @Override
    public void clearForm() {
        number.setText("");
        number.requestFocusInWindow(); // la torre se conserva para registrar varios seguidos
    }

    @Override public void showSuccess(String message) { feedback.success(message); }
    @Override public void showError(String message) { feedback.error(message); }
}
