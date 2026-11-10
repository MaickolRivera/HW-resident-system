package com.hwresident.presentation.gui.swing;

import com.hwresident.presentation.gui.presenter.RecordRow;
import com.hwresident.presentation.gui.presenter.RecordStatus;
import java.awt.Color;
import java.awt.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

/** Tabla de registros de la minuta, reutilizada por Minuta, Dentro del conjunto e Historial. */
final class RecordTable {
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    enum Column {
        REQUESTED("Solicitud", r -> dateTime(r.requestedAt())),
        REQUESTED_TIME("Hora", r -> time(r.requestedAt())),
        PERSON("Persona", RecordRow::person),
        DOCUMENT("Documento", RecordRow::documentNumber),
        TYPE("Tipo", RecordRow::type),
        DESTINATION("Destino", RecordRow::destination),
        REASON("Motivo", RecordRow::reason),
        STATUS("Estado", r -> r.status().label()),
        ENTRY("Entrada", r -> time(r.entryTime())),
        EXIT("Salida", r -> time(r.exitTime())),
        TIME_INSIDE("Tiempo dentro", RecordRow::timeInside),
        VEHICLE("Vehículo", RecordRow::vehicle),
        CORRESPONDENCE("Correspondencia", RecordRow::correspondence),
        GUARDS("Guarda", RecordRow::guards);

        final String header;
        final Function<RecordRow, Object> getter;

        Column(String header, Function<RecordRow, Object> getter) {
            this.header = header;
            this.getter = getter;
        }
    }

    private final RowTableModel<RecordRow> model;
    private final JTable table;

    RecordTable(Column... columns) {
        var list = Arrays.asList(columns);
        model = new RowTableModel<>(list.stream().map(c -> c.header).toList(),
                list.stream().map(c -> c.getter).toList());
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        int status = list.indexOf(Column.STATUS);
        if (status >= 0) {
            table.getColumnModel().getColumn(status).setCellRenderer(new StatusRenderer());
        }
    }

    JScrollPane scroll() {
        return new JScrollPane(table);
    }

    /** Reemplaza las filas conservando la fila seleccionada (por id) si sigue existiendo. */
    void setRows(List<RecordRow> rows) {
        var previous = selected().map(RecordRow::id).orElse(null);
        model.setRows(rows);
        if (previous != null) {
            for (int i = 0; i < rows.size(); i++) {
                if (rows.get(i).id().equals(previous)) {
                    int viewRow = table.convertRowIndexToView(i);
                    table.setRowSelectionInterval(viewRow, viewRow);
                    break;
                }
            }
        }
    }

    Optional<RecordRow> selected() {
        int row = table.getSelectedRow();
        return row < 0 ? Optional.empty() : Optional.of(model.rowAt(table.convertRowIndexToModel(row)));
    }

    private static String dateTime(LocalDateTime value) {
        return value == null ? "" : value.format(DATE_TIME);
    }

    private static String time(LocalDateTime value) {
        return value == null ? "" : value.format(TIME);
    }

    static Color colorOf(RecordStatus status) {
        return switch (status) {
            case PENDING -> new Color(0xB26A00);
            case INSIDE -> new Color(0x1B7F3B);
            case EXITED -> Color.GRAY;
            case REJECTED -> new Color(0xB3261E);
        };
    }

    /** Colorea la columna Estado. */
    private final class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                       boolean focus, int row, int col) {
            var c = super.getTableCellRendererComponent(t, value, selected, focus, row, col);
            if (!selected) {
                c.setForeground(colorOf(model.rowAt(t.convertRowIndexToModel(row)).status()));
            }
            return c;
        }
    }
}
