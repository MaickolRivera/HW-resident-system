package com.hwresident.presentation.gui.swing;

import com.hwresident.presentation.gui.presenter.ApartmentOption;
import com.hwresident.presentation.gui.presenter.HistoryPresenter;
import com.hwresident.presentation.gui.presenter.HistoryView;
import com.hwresident.presentation.gui.presenter.RecordRow;
import com.hwresident.presentation.gui.swing.RecordTable.Column;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Historial completo de la minuta con filtros por documento, apartamento y día. */
final class HistoryPanel extends JPanel implements HistoryView {
    private final JTextField document = new JTextField(12);
    private final JComboBox<ApartmentOption> apartment = new JComboBox<>();
    private final JCheckBox onlyToday = new JCheckBox("Solo hoy");
    private final JLabel summary = new JLabel(" ");
    private final RecordTable table = new RecordTable(Column.REQUESTED, Column.PERSON, Column.DOCUMENT,
            Column.TYPE, Column.DESTINATION, Column.REASON, Column.STATUS, Column.ENTRY, Column.EXIT,
            Column.TIME_INSIDE, Column.VEHICLE, Column.CORRESPONDENCE, Column.GUARDS);
    private HistoryPresenter presenter;

    HistoryPanel() {
        super(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));

        var search = new JButton("Buscar");
        search.addActionListener(e -> presenter.onSearch());
        document.addActionListener(e -> presenter.onSearch());
        var clear = new JButton("Limpiar");
        clear.addActionListener(e -> presenter.onClear());

        var filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.add(new JLabel("Documento:"));
        filters.add(document);
        filters.add(new JLabel("Apartamento:"));
        filters.add(apartment);
        filters.add(onlyToday);
        filters.add(search);
        filters.add(clear);

        var north = new JPanel(new BorderLayout());
        north.add(filters, BorderLayout.WEST);
        north.add(summary, BorderLayout.EAST);

        add(north, BorderLayout.NORTH);
        add(table.scroll(), BorderLayout.CENTER);
    }

    void bind(HistoryPresenter presenter) {
        this.presenter = presenter;
    }

    void onShow() {
        presenter.onShow();
    }

    @Override public String documentFilter() { return document.getText(); }
    @Override public Optional<ApartmentOption> apartmentFilter() { return Optional.ofNullable((ApartmentOption) apartment.getSelectedItem()); }
    @Override public boolean onlyToday() { return onlyToday.isSelected(); }

    @Override
    public void showApartmentOptions(List<ApartmentOption> options) {
        Combos.fill(apartment, options, ApartmentOption::id, true);
    }

    @Override public void showRecords(List<RecordRow> rows) { table.setRows(rows); }
    @Override public void showSummary(String text) { summary.setText(text); }

    @Override
    public void clearFilters() {
        document.setText("");
        onlyToday.setSelected(false);
        if (apartment.getItemCount() > 0) apartment.setSelectedIndex(0);
    }
}
