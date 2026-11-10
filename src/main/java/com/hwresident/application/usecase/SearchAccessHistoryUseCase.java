package com.hwresident.application.usecase;

import com.hwresident.domain.model.AccessRecord;
import com.hwresident.domain.repository.AccessRecordRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/** Historial de la minuta, filtrable por documento, apartamento y día. Más recientes primero. */
public class SearchAccessHistoryUseCase {
    private final AccessRecordRepository records;

    public SearchAccessHistoryUseCase(AccessRecordRepository records) {
        this.records = records;
    }

    /**
     * @param document    parte del documento (vacío o null = todos)
     * @param apartmentId apartamento destino (null = todos)
     * @param day         día de la solicitud (null = todos)
     */
    public List<AccessRecord> execute(String document, UUID apartmentId, LocalDate day) {
        var doc = document == null ? "" : document.trim();
        return records.findAll().stream()
                .filter(r -> doc.isEmpty() || r.person().documentNumber().contains(doc))
                .filter(r -> apartmentId == null || r.destination().id().equals(apartmentId))
                .filter(r -> day == null || r.requestedAt().toLocalDate().equals(day))
                .sorted(Comparator.comparing(AccessRecord::requestedAt).reversed())
                .toList();
    }
}
