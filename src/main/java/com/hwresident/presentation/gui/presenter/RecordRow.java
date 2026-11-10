package com.hwresident.presentation.gui.presenter;

import com.hwresident.domain.model.AccessRecord;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/** Modelo de vista de un registro de la minuta. Las fechas pueden ser null (aún no ocurrieron). */
public record RecordRow(
        UUID id,
        LocalDateTime requestedAt,
        String person,
        String documentNumber,
        String type,
        String destination,
        String reason,
        RecordStatus status,
        LocalDateTime entryTime,
        LocalDateTime exitTime,
        String timeInside,
        String vehicle,
        String correspondence,
        String guards) {

    static RecordRow of(AccessRecord r, LocalDateTime now) {
        var entry = r.entryTime().orElse(null);
        var exit = r.exitTime().orElse(null);
        var guards = "Entrada: " + r.entryGuard().fullName()
                + r.exitGuard().map(g -> " / Salida: " + g.fullName()).orElse("");
        return new RecordRow(
                r.id(),
                r.requestedAt(),
                r.person().fullName(),
                r.person().documentNumber(),
                r.person().typeLabel(),
                r.destination().label(),
                r.reason().orElse(""),
                RecordStatus.valueOf(r.status().name()),
                entry,
                exit,
                entry == null ? "" : duration(entry, exit == null ? now : exit),
                r.vehicle().map(v -> v.label()).orElse(""),
                r.correspondence().orElse(""),
                guards);
    }

    /** Ej.: "45 min", "2 h 05 min". */
    static String duration(LocalDateTime from, LocalDateTime to) {
        long minutes = Math.max(0, Duration.between(from, to).toMinutes());
        return minutes < 60 ? minutes + " min" : String.format("%d h %02d min", minutes / 60, minutes % 60);
    }
}
