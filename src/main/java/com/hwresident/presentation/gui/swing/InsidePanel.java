package com.hwresident.presentation.gui.swing;

import com.hwresident.presentation.gui.presenter.InsidePresenter;
import com.hwresident.presentation.gui.presenter.InsideView;
import com.hwresident.presentation.gui.presenter.RecordRow;
import com.hwresident.presentation.gui.swing.RecordTable.Column;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/** Personas dentro del conjunto (ingresos sin salida). Se actualiza sola cada 30 s mientras está visible. */
final class InsidePanel extends JPanel implements InsideView {
    private static final int REFRESH_MS = 30_000;

    private final ShiftBar shift;
    private final JLabel summary = new JLabel(" ");
    private final JCheckBox includeResidents = new JCheckBox("Incluir residentes");
    private final FeedbackLabel feedback = new FeedbackLabel();
    private final RecordTable table = new RecordTable(Column.PERSON, Column.DOCUMENT, Column.TYPE,
            Column.DESTINATION, Column.REASON, Column.ENTRY, Column.TIME_INSIDE, Column.VEHICLE, Column.GUARDS);
    private InsidePresenter presenter;

    InsidePanel(ShiftBar shift) {
        super(new BorderLayout(0, 12));
        this.shift = shift;
        setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));

        summary.setFont(summary.getFont().deriveFont(Font.BOLD, 16f));
        includeResidents.addActionListener(e -> presenter.onShow());
        var refresh = new JButton("Actualizar");
        refresh.addActionListener(e -> presenter.onShow());
        var options = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        options.add(includeResidents);
        options.add(refresh);
        var north = new JPanel(new BorderLayout());
        north.add(summary, BorderLayout.WEST);
        north.add(options, BorderLayout.EAST);

        var exit = new JButton("Dar salida");
        exit.addActionListener(e -> presenter.onExit());
        var south = new JPanel(new BorderLayout());
        south.add(feedback, BorderLayout.CENTER);
        south.add(exit, BorderLayout.EAST);

        add(north, BorderLayout.NORTH);
        add(table.scroll(), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        new Timer(REFRESH_MS, e -> {
            if (isShowing() && presenter != null) presenter.onShow();
        }).start();
    }

    void bind(InsidePresenter presenter) {
        this.presenter = presenter;
    }

    void onShow() {
        feedback.clear();
        presenter.onShow();
    }

    @Override public String guardDocument() { return shift.guardDocument(); }
    @Override public boolean includeResidents() { return includeResidents.isSelected(); }
    @Override public Optional<RecordRow> selectedRecord() { return table.selected(); }
    @Override public void showRecords(List<RecordRow> rows) { table.setRows(rows); }
    @Override public void showSummary(String text) { summary.setText(text); }
    @Override public void showSuccess(String message) { feedback.success(message); }
    @Override public void showError(String message) { feedback.error(message); }
}
