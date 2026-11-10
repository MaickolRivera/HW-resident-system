package com.hwresident.presentation.gui.swing;

import java.util.List;
import java.util.function.Function;
import javax.swing.table.AbstractTableModel;

/** TableModel genérico de solo lectura: cada columna se define con una función fila -> valor. */
final class RowTableModel<T> extends AbstractTableModel {
    private final List<String> columns;
    private final List<Function<T, Object>> getters;
    private List<T> rows = List.of();

    RowTableModel(List<String> columns, List<Function<T, Object>> getters) {
        this.columns = columns;
        this.getters = getters;
    }

    void setRows(List<T> rows) {
        this.rows = List.copyOf(rows);
        fireTableDataChanged();
    }

    T rowAt(int index) {
        return rows.get(index);
    }

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return columns.size(); }
    @Override public String getColumnName(int column) { return columns.get(column); }
    @Override public Object getValueAt(int row, int column) { return getters.get(column).apply(rows.get(row)); }
}
