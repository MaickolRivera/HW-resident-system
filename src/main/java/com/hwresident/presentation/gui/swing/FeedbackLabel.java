package com.hwresident.presentation.gui.swing;

import java.awt.Color;
import javax.swing.JLabel;

/** Línea de mensaje inline: verde para éxito, rojo para error. */
final class FeedbackLabel extends JLabel {
    private static final Color OK = new Color(0x1B7F3B);
    private static final Color ERROR = new Color(0xB3261E);

    FeedbackLabel() {
        super(" ");
    }

    void success(String message) {
        setForeground(OK);
        setText(message);
    }

    void error(String message) {
        setForeground(ERROR);
        setText(message);
    }

    void clear() {
        setText(" ");
    }
}
