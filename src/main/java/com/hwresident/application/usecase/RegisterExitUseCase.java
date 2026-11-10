package com.hwresident.application.usecase;

import com.hwresident.domain.exception.DomainException;
import com.hwresident.domain.model.AccessRecord;
import com.hwresident.domain.model.Guard;
import com.hwresident.domain.repository.AccessRecordRepository;
import com.hwresident.domain.repository.PersonRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

/** Registrar salida: DENTRO -> SALIDA_REGISTRADA, guardando la hora y el guarda que la registra. */
public class RegisterExitUseCase {
    private final PersonRepository persons;
    private final AccessRecordRepository records;
    private final Clock clock;

    public RegisterExitUseCase(PersonRepository persons, AccessRecordRepository records, Clock clock) {
        this.persons = persons;
        this.records = records;
        this.clock = clock;
    }

    public AccessRecord execute(UUID recordId, String guardDocument) {
        var guard = persons.findByDocumentNumber(guardDocument == null ? "" : guardDocument.trim(), Guard.class)
                .orElseThrow(() -> new DomainException("Seleccione el guarda de turno"));
        var record = records.findById(recordId)
                .orElseThrow(() -> new DomainException("El registro no existe"));
        record.registerExit(LocalDateTime.now(clock).withSecond(0).withNano(0), guard);
        return records.save(record);
    }
}
