package com.hwresident.application.usecase;

import com.hwresident.domain.exception.DomainException;
import com.hwresident.domain.model.AccessRecord;
import com.hwresident.domain.repository.AccessRecordRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

/** "¿El residente autoriza?": EN_ESPERA -> DENTRO (sí) o EN_ESPERA -> RECHAZADO (no). */
public class ResolveAuthorizationUseCase {
    private final AccessRecordRepository records;
    private final Clock clock;

    public ResolveAuthorizationUseCase(AccessRecordRepository records, Clock clock) {
        this.records = records;
        this.clock = clock;
    }

    public AccessRecord execute(UUID recordId, boolean authorized) {
        var record = records.findById(recordId)
                .orElseThrow(() -> new DomainException("El registro no existe"));
        if (authorized) {
            record.admit(LocalDateTime.now(clock).withSecond(0).withNano(0));
        } else {
            record.reject();
        }
        return records.save(record);
    }
}
