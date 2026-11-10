package com.hwresident.application.usecase;

import com.hwresident.domain.model.AccessRecord;
import com.hwresident.domain.model.AccessStatus;
import com.hwresident.domain.model.ExternalPerson;
import com.hwresident.domain.repository.AccessRecordRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/** "Personas dentro del conjunto": ingresos registrados que aún no tienen salida. */
public class ListPeopleInsideUseCase {
    private final AccessRecordRepository records;

    public ListPeopleInsideUseCase(AccessRecordRepository records) {
        this.records = records;
    }

    /** @param includeResidents false = solo externos (visitantes y prestadores de servicio) */
    public List<AccessRecord> execute(boolean includeResidents) {
        return records.findAll().stream()
                .filter(r -> r.status() == AccessStatus.INSIDE)
                .filter(r -> includeResidents || r.person() instanceof ExternalPerson)
                .sorted(Comparator.comparing(r -> r.entryTime().orElse(LocalDateTime.MIN)))
                .toList();
    }
}
