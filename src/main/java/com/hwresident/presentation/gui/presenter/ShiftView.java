package com.hwresident.presentation.gui.presenter;

import java.util.List;

/** Selector del guarda de turno (compartido por la minuta y la vista de personas dentro). */
public interface ShiftView {
    void showGuards(List<PersonOption> guards);
}
