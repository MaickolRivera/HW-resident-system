package com.hwresident.presentation.gui.swing;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

/** Selector de fecha y hora (yyyy-MM-dd HH:mm) basado en JSpinner; expone LocalDateTime. */
final class DateTimeField extends JSpinner {
    static final String PATTERN = "yyyy-MM-dd HH:mm";

    DateTimeField(LocalDateTime initial) {
        super(new SpinnerDateModel());
        setEditor(new DateEditor(this, PATTERN));
        setValue(initial);
    }

    LocalDateTime value() {
        try {
            commitEdit(); // toma lo que el usuario escribió aunque no haya perdido el foco
        } catch (ParseException ignored) {
            // texto inválido: se conserva el último valor válido del modelo
        }
        return LocalDateTime.ofInstant(((Date) super.getValue()).toInstant(), ZoneId.systemDefault())
                .withSecond(0).withNano(0);
    }

    void setValue(LocalDateTime value) {
        super.setValue(Date.from(value.atZone(ZoneId.systemDefault()).toInstant()));
    }
}
