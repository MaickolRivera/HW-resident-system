package com.hwresident.presentation.gui.swing;

import com.hwresident.presentation.gui.presenter.ApartmentOption;
import com.hwresident.presentation.gui.presenter.LogbookPresenter;
import com.hwresident.presentation.gui.presenter.LogbookView;
import com.hwresident.presentation.gui.presenter.RecordRow;
import com.hwresident.presentation.gui.swing.RecordTable.Column;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Minuta digital: formulario de ingreso + movimientos del día con Autorizar / Rechazar / Dar salida. */
final class LogbookPanel extends JPanel implements LogbookView {
    private static final String LOOKUP_HINT = "Digite el documento y presione Enter o Buscar";

    private final ShiftBar shift;
    private final JTextField document = new JTextField(16);
    private final JLabel lookup = new JLabel(LOOKUP_HINT);
    private final JTextField name = new JTextField(16);
    private final JComboBox<String> personType = new JComboBox<>();
    private final JTextField company = new JTextField(16);
    private final JTextField labor = new JTextField(16);
    private final JComboBox<ApartmentOption> destination = new JComboBox<>();
    private final JTextField reason = new JTextField(16);
    private final JTextField plate = new JTextField(8);
    private final JComboBox<String> vehicleType = new JComboBox<>();
    private final JTextField correspondence = new JTextField(16);
    private final FeedbackLabel feedback = new FeedbackLabel();
    private final RecordTable table = new RecordTable(Column.REQUESTED_TIME, Column.PERSON, Column.DOCUMENT,
            Column.TYPE, Column.DESTINATION, Column.REASON, Column.STATUS, Column.ENTRY, Column.EXIT,
            Column.VEHICLE, Column.CORRESPONDENCE, Column.GUARDS);
    private LogbookPresenter presenter;

    LogbookPanel(ShiftBar shift) {
        super(new BorderLayout(0, 12));
        this.shift = shift;
        setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));

        // --- Quién llega
        var search = new JButton("Buscar");
        search.addActionListener(e -> presenter.onLookup());
        document.addActionListener(e -> presenter.onLookup());
        document.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { resetLookup(); }
            @Override public void removeUpdate(DocumentEvent e) { resetLookup(); }
            @Override public void changedUpdate(DocumentEvent e) { resetLookup(); }
        });
        var documentRow = new JPanel(new BorderLayout(8, 0));
        documentRow.add(document, BorderLayout.CENTER);
        documentRow.add(search, BorderLayout.EAST);
        lookup.setFont(lookup.getFont().deriveFont(Font.ITALIC));
        personType.addActionListener(e -> updateServiceFields());

        var who = Ui.form();
        who.setBorder(BorderFactory.createTitledBorder("Quién llega"));
        Ui.addRow(who, 0, "Documento", documentRow);
        Ui.addRow(who, 1, "", lookup);
        Ui.addRow(who, 2, "Nombre (si es nuevo)", name);
        Ui.addRow(who, 3, "Tipo (si es nuevo)", personType);
        Ui.addRow(who, 4, "Empresa", company);
        Ui.addRow(who, 5, "Labor", labor);

        // --- La visita
        var plateRow = new JPanel(new BorderLayout(8, 0));
        plateRow.add(plate, BorderLayout.CENTER);
        plateRow.add(vehicleType, BorderLayout.EAST);
        var visit = Ui.form();
        visit.setBorder(BorderFactory.createTitledBorder("Visita"));
        Ui.addRow(visit, 0, "Apartamento destino", destination);
        Ui.addRow(visit, 1, "Motivo", reason);
        Ui.addRow(visit, 2, "Vehículo (placa)", plateRow);
        Ui.addRow(visit, 3, "Correspondencia", correspondence);

        var forms = new JPanel(new GridLayout(1, 2, 12, 0));
        forms.add(who);
        forms.add(visit);

        var register = new JButton("Registrar ingreso");
        register.setFont(register.getFont().deriveFont(Font.BOLD, 14f));
        register.addActionListener(e -> presenter.onRegisterEntry());
        var registerRow = new JPanel(new BorderLayout(12, 0));
        registerRow.add(feedback, BorderLayout.CENTER);
        registerRow.add(register, BorderLayout.EAST);

        var north = new JPanel(new BorderLayout(0, 8));
        north.add(forms, BorderLayout.CENTER);
        north.add(registerRow, BorderLayout.SOUTH);

        // --- Movimientos
        var authorize = new JButton("Autorizar");
        authorize.addActionListener(e -> presenter.onAuthorize());
        var reject = new JButton("Rechazar");
        reject.addActionListener(e -> presenter.onReject());
        var exit = new JButton("Dar salida");
        exit.addActionListener(e -> presenter.onExit());
        var actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.add(new JLabel("Registro seleccionado:"));
        actions.add(authorize);
        actions.add(reject);
        actions.add(exit);

        var title = new JLabel("Movimientos de hoy (y solicitudes abiertas)");
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        var center = new JPanel(new BorderLayout(0, 6));
        center.add(title, BorderLayout.NORTH);
        center.add(table.scroll(), BorderLayout.CENTER);
        center.add(actions, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        updateServiceFields();
    }

    void bind(LogbookPresenter presenter) {
        this.presenter = presenter;
    }

    void onShow() {
        presenter.onShow();
    }

    @Override public String guardDocument() { return shift.guardDocument(); }
    @Override public String documentNumber() { return document.getText(); }
    @Override public String fullName() { return name.getText(); }
    @Override public String personType() { return (String) personType.getSelectedItem(); }
    @Override public String company() { return company.getText(); }
    @Override public String labor() { return labor.getText(); }
    @Override public Optional<ApartmentOption> selectedDestination() { return Optional.ofNullable((ApartmentOption) destination.getSelectedItem()); }
    @Override public String reason() { return reason.getText(); }
    @Override public String vehiclePlate() { return plate.getText(); }
    @Override public String vehicleType() { return (String) vehicleType.getSelectedItem(); }
    @Override public String correspondence() { return correspondence.getText(); }
    @Override public Optional<RecordRow> selectedRecord() { return table.selected(); }

    @Override
    public void showApartmentOptions(List<ApartmentOption> options) {
        Combos.fill(destination, options, ApartmentOption::id, false);
    }

    @Override public void showPersonTypes(List<String> types) { Combos.fillStrings(personType, types); }
    @Override public void showVehicleTypes(List<String> types) { Combos.fillStrings(vehicleType, types); }

    @Override
    public void showPersonLookup(String message, boolean registered) {
        feedback.clear();
        lookup.setText(message);
        lookup.setForeground(registered ? new Color(0x1B7F3B) : new Color(0xB26A00));
        setNewPersonFieldsEnabled(!registered);
    }

    @Override
    public void setDestination(UUID apartmentId, boolean editable) {
        if (apartmentId != null) {
            for (int i = 0; i < destination.getItemCount(); i++) {
                if (apartmentId.equals(destination.getItemAt(i).id())) {
                    destination.setSelectedIndex(i);
                }
            }
        }
        destination.setEnabled(editable);
    }

    @Override public void showRecords(List<RecordRow> rows) { table.setRows(rows); }

    @Override
    public Optional<Boolean> askResidentAuthorization(String question) {
        Object[] options = {"Sí, autoriza", "No autoriza", "Decidir después"};
        int answer = JOptionPane.showOptionDialog(this, question, "Autorización del residente",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        return switch (answer) {
            case 0 -> Optional.of(true);
            case 1 -> Optional.of(false);
            default -> Optional.empty();
        };
    }

    @Override
    public void clearForm() {
        for (var field : List.of(document, name, company, labor, reason, plate, correspondence)) {
            field.setText("");
        }
        personType.setSelectedIndex(personType.getItemCount() > 0 ? 0 : -1);
        vehicleType.setSelectedIndex(vehicleType.getItemCount() > 0 ? 0 : -1);
        destination.setSelectedIndex(-1);
        resetLookup();
        document.requestFocusInWindow();
    }

    @Override public void showSuccess(String message) { feedback.success(message); }
    @Override public void showError(String message) { feedback.error(message); }

    /** Al cambiar el documento se olvida la búsqueda anterior. */
    private void resetLookup() {
        lookup.setText(LOOKUP_HINT);
        lookup.setForeground(Color.GRAY);
        destination.setEnabled(true);
        setNewPersonFieldsEnabled(true);
    }

    private void setNewPersonFieldsEnabled(boolean enabled) {
        name.setEnabled(enabled);
        personType.setEnabled(enabled);
        updateServiceFields();
    }

    /** Empresa y labor solo aplican a prestadores de servicio nuevos (el primer tipo es "Visitante"). */
    private void updateServiceFields() {
        boolean provider = personType.isEnabled() && personType.getSelectedIndex() > 0;
        company.setEnabled(provider);
        labor.setEnabled(provider);
    }
}
