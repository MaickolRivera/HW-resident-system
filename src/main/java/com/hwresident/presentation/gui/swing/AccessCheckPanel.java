package com.hwresident.presentation.gui.swing;

import com.hwresident.presentation.gui.presenter.AccessCheckPresenter;
import com.hwresident.presentation.gui.presenter.AccessCheckView;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalDateTime;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/** Pantalla de portería: documento + momento -> semáforo verde/rojo. */
final class AccessCheckPanel extends JPanel implements AccessCheckView {
    private static final Color ALLOWED = new Color(0x1B7F3B);
    private static final Color DENIED = new Color(0xB3261E);

    private final JTextField document = new JTextField(24);
    private final DateTimeField moment = new DateTimeField(LocalDateTime.now());
    private final JLabel result = new JLabel("Ingrese un documento y verifique", SwingConstants.CENTER);
    private final FeedbackLabel feedback = new FeedbackLabel();
    private AccessCheckPresenter presenter;

    AccessCheckPanel() {
        super(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        var form = Ui.form();
        Ui.addRow(form, 0, "Documento", document);
        var now = new JButton("Ahora");
        now.addActionListener(e -> moment.setValue(LocalDateTime.now()));
        var momentRow = new JPanel(new BorderLayout(8, 0));
        momentRow.add(moment, BorderLayout.CENTER);
        momentRow.add(now, BorderLayout.EAST);
        Ui.addRow(form, 1, "Fecha y hora", momentRow);
        var check = new JButton("Verificar acceso");
        check.addActionListener(e -> presenter.onCheck());
        document.addActionListener(e -> presenter.onCheck());

        var north = new JPanel(new BorderLayout(0, 8));
        north.add(form, BorderLayout.NORTH);
        north.add(check, BorderLayout.EAST);
        north.add(feedback, BorderLayout.SOUTH);

        result.setOpaque(true);
        result.setFont(result.getFont().deriveFont(Font.BOLD, 20f));
        result.setBorder(BorderFactory.createEmptyBorder(24, 12, 24, 12));
        result.setBackground(new Color(0xEEEEEE));

        add(north, BorderLayout.NORTH);
        add(result, BorderLayout.CENTER);
    }

    void bind(AccessCheckPresenter presenter) {
        this.presenter = presenter;
    }

    @Override public String visitorDocument() { return document.getText(); }
    @Override public LocalDateTime moment() { return moment.value(); }

    @Override
    public void showAccessResult(boolean allowed, String message) {
        feedback.clear();
        result.setText(message);
        result.setBackground(allowed ? ALLOWED : DENIED);
        result.setForeground(Color.WHITE);
    }

    @Override
    public void showError(String message) {
        feedback.error(message);
    }
}
