package com.hwresident.presentation.gui.presenter;

import java.time.LocalDateTime;
import java.util.UUID;

/** Modelo de vista de un permiso, ya con nombres resueltos y estado calculado. */
public record PermissionRow(
        UUID id,
        String residentLabel,
        String visitorLabel,
        String apartmentLabel,
        LocalDateTime validFrom,
        LocalDateTime validUntil,
        PermissionStatus status) {}
