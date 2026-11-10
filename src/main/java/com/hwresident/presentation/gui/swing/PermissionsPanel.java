package com.hwresident.presentation.gui.swing;

import com.hwresident.presentation.gui.presenter.PermissionRow;
import com.hwresident.presentation.gui.presenter.PermissionsPresenter;
import com.hwresident.presentation.gui.presenter.PermissionsView;
import com.hwresident.presentation.gui.presenter.PersonOption;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

/** Permisos anticipados: el residente autoriza a una persona externa a entrar a su apartamento. */
final class PermissionsPanel extends JPanel implements PermissionsView {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern(DateTimeField.PATTERN);
    private static final int STATUS_COLUMN = 5;

    private final JComboBox<PersonOption> resident = new JComboBox<>();
    private final JComboBox<PersonOption> visitor = new JComboBox<>();
    private final DateTimeField from = new DateTimeField(LocalDateTime.now());
    private final DateTimeField until = new DateTimeField(LocalDateTime.now().plusHours(4));
    private final FeedbackLabel feedback = new FeedbackLabel();
    private final RowTableModel<PermissionRow> model = new RowTableModel<>(
            List.of("Residente", "Persona externa", "Apartamento", "Desde", "Hasta", "Estado"),
            List.of(PermissionRow::residentLabel,
                    PermissionRow::visitorLabel,
                    PermissionRow::apartmentLabel,
                    p -> p.validFrom().format(FORMAT),
                    p -> p.validUntil().format(FORMAT),
                    p -> p.status().label()));
    private final JTable table = new JTable(model);
    private PermissionsPresenter presenter;

    PermissionsPanel() {
        super(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        var form = Ui.form();
        Ui.addRow(form, 0, "Residente que autoriza", resident);
        Ui.addRow(form, 1, "Persona externa", visitor);
        Ui.addRow(form, 2, "Válido desde", from);
        Ui.addRow(form, 3, "Válido hasta", until);
        var grant = new JButton("Otorgar permiso");
        grant.addActionListener(e -> presenter.onGrant());

        var north = new JPanel(new BorderLayout(0, 8));
        north.add(form, BorderLayout.NORTH);
        north.add(grant, BorderLayout.EAST);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(STATUS_COLUMN).setCellRenderer(new StatusRenderer());
        var revoke = new JButton("Revocar permiso seleccionado");
        revoke.addActionListener(e -> presenter.onRevoke());

        var south = new JPanel(new BorderLayout());
        south.add(feedback, BorderLayout.CENTER);
        south.add(revoke, BorderLayout.EAST);

        var center = new JPanel(new BorderLayout(0, 8));
        center.add(north, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
    }

    void bind(PermissionsPresenter presenter) {
        this.presenter = presenter;
    }

    void onShow() {
        feedback.clear();
        presenter.onShow();
    }

    @Override public Optional<PersonOption> selectedResident() { return Optional.ofNullable((PersonOption) resident.getSelectedItem()); }
    @Override public Optional<PersonOption> selectedVisitor() { return Optional.ofNullable((PersonOption) visitor.getSelectedItem()); }
    @Override public LocalDateTime validFrom() { return from.value(); }
    @Override public LocalDateTime validUntil() { return until.value(); }

    @Override
    public Optional<PermissionRow> selectedPermission() {
        int row = table.getSelectedRow();
        return row < 0 ? Optional.empty() : Optional.of(model.rowAt(table.convertRowIndexToModel(row)));
    }

    @Override public void showResidentOptions(List<PersonOption> options) { Combos.fill(resident, options, PersonOption::documentNumber, true); }
    @Override public void showVisitorOptions(List<PersonOption> options) { Combos.fill(visitor, options, PersonOption::documentNumber, true); }
    @Override public void showPermissions(List<PermissionRow> rows) { model.setRows(rows); }

    @Override
    public boolean confirmRevoke(PermissionRow permission) {
        return JOptionPane.showConfirmDialog(this,
                "¿Revocar el permiso de " + permission.visitorLabel() + "?",
                "Confirmar revocación", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    @Override public void showSuccess(String message) { feedback.success(message); }
    @Override public void showError(String message) { feedback.error(message); }

    /** Colorea la columna Estado según el estado del permiso. */
    private final class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                       boolean focus, int row, int col) {
            var c = super.getTableCellRendererComponent(t, value, selected, focus, row, col);
            var status = model.rowAt(t.convertRowIndexToModel(row)).status();
            if (!selected) {
                c.setForeground(switch (status) {
                    case ACTIVE -> new Color(0x1B7F3B);
                    case SCHEDULED -> new Color(0x1F5FBF);
                    case EXPIRED -> Color.GRAY;
                    case REVOKED -> new Color(0xB3261E);
                });
            }
            return c;
        }
    }
}
