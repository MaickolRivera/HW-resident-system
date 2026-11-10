package com.hwresident.presentation.gui;

import com.hwresident.application.usecase.CheckAccessUseCase;
import com.hwresident.application.usecase.GrantAccessPermissionUseCase;
import com.hwresident.application.usecase.ListAccessPermissionsUseCase;
import com.hwresident.application.usecase.ListResidentsUseCase;
import com.hwresident.application.usecase.ListVisitorsUseCase;
import com.hwresident.application.usecase.RegisterResidentUseCase;
import com.hwresident.application.usecase.RegisterVisitorUseCase;
import com.hwresident.application.usecase.RevokeAccessPermissionUseCase;

/** Casos de uso que la GUI necesita; los arma el composition root (Main). */
public record GuiUseCases(
        RegisterResidentUseCase registerResident,
        ListResidentsUseCase listResidents,
        RegisterVisitorUseCase registerVisitor,
        ListVisitorsUseCase listVisitors,
        GrantAccessPermissionUseCase grantPermission,
        RevokeAccessPermissionUseCase revokePermission,
        ListAccessPermissionsUseCase listPermissions,
        CheckAccessUseCase checkAccess) {}
