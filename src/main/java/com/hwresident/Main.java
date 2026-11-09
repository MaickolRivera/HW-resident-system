package com.hwresident;

import com.hwresident.application.usecase.CheckAccessUseCase;
import com.hwresident.application.usecase.GrantAccessPermissionUseCase;
import com.hwresident.application.usecase.ListAccessPermissionsUseCase;
import com.hwresident.application.usecase.ListResidentsUseCase;
import com.hwresident.application.usecase.ListVisitorsUseCase;
import com.hwresident.application.usecase.RegisterResidentUseCase;
import com.hwresident.application.usecase.RegisterVisitorUseCase;
import com.hwresident.application.usecase.RevokeAccessPermissionUseCase;
import com.hwresident.infrastructure.persistence.InMemoryAccessPermissionRepository;
import com.hwresident.infrastructure.persistence.InMemoryResidentRepository;
import com.hwresident.infrastructure.persistence.InMemoryVisitorRepository;
import com.hwresident.presentation.cli.MainMenuCli;
import com.hwresident.presentation.cli.ResidentCli;
import com.hwresident.presentation.cli.VisitorCli;
import com.hwresident.presentation.gui.GuiUseCases;
import com.hwresident.presentation.gui.swing.SwingApp;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Composition root: único lugar que conoce todas las capas.
 * Por defecto abre la GUI (Swing); con --cli, o sin entorno gráfico, usa la consola.
 */
public class Main {
    public static void main(String[] args) {
        var residentRepository = new InMemoryResidentRepository();
        var visitorRepository = new InMemoryVisitorRepository();
        var permissionRepository = new InMemoryAccessPermissionRepository();

        var registerResident = new RegisterResidentUseCase(residentRepository);
        var listResidents = new ListResidentsUseCase(residentRepository);
        var registerVisitor = new RegisterVisitorUseCase(visitorRepository);
        var listVisitors = new ListVisitorsUseCase(visitorRepository);

        boolean forceCli = Arrays.asList(args).contains("--cli");
        if (!forceCli && !GraphicsEnvironment.isHeadless()) {
            SwingApp.launch(new GuiUseCases(
                    registerResident,
                    listResidents,
                    registerVisitor,
                    listVisitors,
                    new GrantAccessPermissionUseCase(residentRepository, visitorRepository, permissionRepository),
                    new RevokeAccessPermissionUseCase(permissionRepository),
                    new ListAccessPermissionsUseCase(permissionRepository),
                    new CheckAccessUseCase(visitorRepository, permissionRepository)));
            return;
        }

        if (!forceCli) {
            System.err.println("No hay entorno gráfico disponible; iniciando modo consola.");
        }
        var scanner = new Scanner(System.in);
        var residentCli = new ResidentCli(registerResident, listResidents, scanner);
        var visitorCli = new VisitorCli(registerVisitor, listVisitors, scanner);
        new MainMenuCli(residentCli, visitorCli, scanner).run();
    }
}
