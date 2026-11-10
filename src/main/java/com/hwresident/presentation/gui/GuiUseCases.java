package com.hwresident.presentation.gui;

import com.hwresident.application.usecase.CheckAccessUseCase;
import com.hwresident.application.usecase.FindPersonUseCase;
import com.hwresident.application.usecase.GrantAccessPermissionUseCase;
import com.hwresident.application.usecase.ListAccessPermissionsUseCase;
import com.hwresident.application.usecase.ListApartmentsUseCase;
import com.hwresident.application.usecase.ListExternalPersonsUseCase;
import com.hwresident.application.usecase.ListGuardsUseCase;
import com.hwresident.application.usecase.ListPeopleInsideUseCase;
import com.hwresident.application.usecase.ListResidentsUseCase;
import com.hwresident.application.usecase.RegisterApartmentUseCase;
import com.hwresident.application.usecase.RegisterEntryUseCase;
import com.hwresident.application.usecase.RegisterExitUseCase;
import com.hwresident.application.usecase.RegisterGuardUseCase;
import com.hwresident.application.usecase.RegisterResidentUseCase;
import com.hwresident.application.usecase.RegisterServiceProviderUseCase;
import com.hwresident.application.usecase.RegisterVisitorUseCase;
import com.hwresident.application.usecase.ResolveAuthorizationUseCase;
import com.hwresident.application.usecase.RevokeAccessPermissionUseCase;
import com.hwresident.application.usecase.SearchAccessHistoryUseCase;
import java.time.Clock;

/** Casos de uso que la GUI necesita; los arma el composition root (Main). */
public record GuiUseCases(
        RegisterApartmentUseCase registerApartment,
        ListApartmentsUseCase listApartments,
        RegisterResidentUseCase registerResident,
        ListResidentsUseCase listResidents,
        RegisterVisitorUseCase registerVisitor,
        RegisterServiceProviderUseCase registerServiceProvider,
        ListExternalPersonsUseCase listExternals,
        RegisterGuardUseCase registerGuard,
        ListGuardsUseCase listGuards,
        FindPersonUseCase findPerson,
        GrantAccessPermissionUseCase grantPermission,
        RevokeAccessPermissionUseCase revokePermission,
        ListAccessPermissionsUseCase listPermissions,
        CheckAccessUseCase checkAccess,
        RegisterEntryUseCase registerEntry,
        ResolveAuthorizationUseCase resolveAuthorization,
        RegisterExitUseCase registerExit,
        ListPeopleInsideUseCase listPeopleInside,
        SearchAccessHistoryUseCase searchHistory,
        Clock clock) {}
