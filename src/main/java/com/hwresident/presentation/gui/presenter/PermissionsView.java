package com.hwresident.presentation.gui.presenter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Contrato que el presentador necesita de la pantalla de permisos. */
public interface PermissionsView {
    Optional<PersonOption> selectedResident();
    Optional<PersonOption> selectedVisitor();
    LocalDateTime validFrom();
    LocalDateTime validUntil();
    Optional<PermissionRow> selectedPermission();

    void showResidentOptions(List<PersonOption> options);
    void showVisitorOptions(List<PersonOption> options);
    void showPermissions(List<PermissionRow> rows);
    boolean confirmRevoke(PermissionRow permission);
    void showSuccess(String message);
    void showError(String message);
}
