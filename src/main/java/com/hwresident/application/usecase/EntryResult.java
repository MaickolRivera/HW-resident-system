package com.hwresident.application.usecase;

import com.hwresident.domain.model.AccessRecord;

/** Resultado de registrar un ingreso en la minuta. */
public record EntryResult(AccessRecord record, Outcome outcome) {

    public enum Outcome {
        /** Residente: entra directo, sin autorización. */
        RESIDENT_ENTERED,
        /** Externo con permiso vigente para ese apartamento: entra directo. */
        PRE_AUTHORIZED,
        /** Externo sin permiso: queda EN_ESPERA hasta que el residente autorice o rechace. */
        AWAITING_AUTHORIZATION
    }
}
