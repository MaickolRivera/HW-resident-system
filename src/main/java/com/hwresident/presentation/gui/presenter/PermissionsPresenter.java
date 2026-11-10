package com.hwresident.presentation.gui.presenter;

import com.hwresident.application.usecase.GrantAccessPermissionUseCase;
import com.hwresident.application.usecase.ListAccessPermissionsUseCase;
import com.hwresident.application.usecase.ListApartmentsUseCase;
import com.hwresident.application.usecase.ListExternalPersonsUseCase;
import com.hwresident.application.usecase.ListResidentsUseCase;
import com.hwresident.application.usecase.RevokeAccessPermissionUseCase;
import com.hwresident.domain.exception.DomainException;
import com.hwresident.domain.model.AccessPermission;
import com.hwresident.domain.model.Apartment;
import com.hwresident.domain.model.ExternalPerson;
import com.hwresident.domain.model.Resident;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PermissionsPresenter {
    private static final String UNKNOWN = "(desconocido)";

    private final PermissionsView view;
    private final ListResidentsUseCase listResidents;
    private final ListExternalPersonsUseCase listExternals;
    private final ListApartmentsUseCase listApartments;
    private final GrantAccessPermissionUseCase grant;
    private final RevokeAccessPermissionUseCase revoke;
    private final ListAccessPermissionsUseCase listPermissions;
    private final Clock clock;

    public PermissionsPresenter(PermissionsView view,
                                ListResidentsUseCase listResidents,
                                ListExternalPersonsUseCase listExternals,
                                ListApartmentsUseCase listApartments,
                                GrantAccessPermissionUseCase grant,
                                RevokeAccessPermissionUseCase revoke,
                                ListAccessPermissionsUseCase listPermissions,
                                Clock clock) {
        this.view = view;
        this.listResidents = listResidents;
        this.listExternals = listExternals;
        this.listApartments = listApartments;
        this.grant = grant;
        this.revoke = revoke;
        this.listPermissions = listPermissions;
        this.clock = clock;
    }

    /** Refresca combos y tabla (se llama cada vez que la pantalla se muestra). */
    public void onShow() {
        view.showResidentOptions(listResidents.execute().stream()
                .map(r -> new PersonOption(r.documentNumber(), residentLabel(r)))
                .toList());
        view.showVisitorOptions(listExternals.execute().stream()
                .map(v -> new PersonOption(v.documentNumber(), externalLabel(v)))
                .toList());
        refreshTable();
    }

    public void onGrant() {
        var resident = view.selectedResident();
        var visitor = view.selectedVisitor();
        if (resident.isEmpty()) {
            view.showError("Seleccione un residente (regístrelo primero si no existe)");
            return;
        }
        if (visitor.isEmpty()) {
            view.showError("Seleccione un visitante (regístrelo primero si no existe)");
            return;
        }
        try {
            grant.execute(resident.get().documentNumber(), visitor.get().documentNumber(),
                    view.validFrom(), view.validUntil());
            refreshTable();
            view.showSuccess("Permiso otorgado");
        } catch (DomainException e) {
            view.showError(e.getMessage());
        }
    }

    public void onRevoke() {
        var selected = view.selectedPermission();
        if (selected.isEmpty()) {
            view.showError("Seleccione un permiso de la tabla");
            return;
        }
        if (!view.confirmRevoke(selected.get())) {
            return;
        }
        try {
            revoke.execute(selected.get().id());
            refreshTable();
            view.showSuccess("Permiso revocado");
        } catch (DomainException e) {
            view.showError(e.getMessage());
        }
    }

    private void refreshTable() {
        Map<UUID, String> residentNames = listResidents.execute().stream()
                .collect(Collectors.toMap(Resident::id, PermissionsPresenter::residentLabel));
        Map<UUID, String> externalNames = listExternals.execute().stream()
                .collect(Collectors.toMap(ExternalPerson::id, PermissionsPresenter::externalLabel));
        Map<UUID, String> apartmentNames = listApartments.execute().stream()
                .collect(Collectors.toMap(Apartment::id, Apartment::label));
        var now = LocalDateTime.now(clock);
        view.showPermissions(listPermissions.execute().stream()
                .map(p -> new PermissionRow(
                        p.id(),
                        residentNames.getOrDefault(p.residentId(), UNKNOWN),
                        externalNames.getOrDefault(p.externalId(), UNKNOWN),
                        apartmentNames.getOrDefault(p.apartmentId(), UNKNOWN),
                        p.validFrom(),
                        p.validUntil(),
                        statusOf(p, now)))
                .toList());
    }

    /** Reutiliza la regla del dominio (allowsEntryAt) en lugar de duplicarla. */
    private static PermissionStatus statusOf(AccessPermission permission, LocalDateTime now) {
        if (permission.isRevoked()) return PermissionStatus.REVOKED;
        if (permission.allowsEntryAt(now)) return PermissionStatus.ACTIVE;
        return now.isBefore(permission.validFrom()) ? PermissionStatus.SCHEDULED : PermissionStatus.EXPIRED;
    }

    private static String residentLabel(Resident r) {
        return r.displayName() + " - " + r.apartment().label();
    }

    private static String externalLabel(ExternalPerson p) {
        return p.displayName() + " - " + p.typeLabel();
    }
}
