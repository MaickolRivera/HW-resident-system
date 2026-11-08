package com.hwresident;

import com.hwresident.application.usecase.ListResidentsUseCase;
import com.hwresident.application.usecase.ListVisitorsUseCase;
import com.hwresident.application.usecase.RegisterResidentUseCase;
import com.hwresident.application.usecase.RegisterVisitorUseCase;
import com.hwresident.infrastructure.persistence.InMemoryResidentRepository;
import com.hwresident.infrastructure.persistence.InMemoryVisitorRepository;
import com.hwresident.presentation.cli.MainMenuCli;
import com.hwresident.presentation.cli.ResidentCli;
import com.hwresident.presentation.cli.VisitorCli;
import java.util.Scanner;

/** Composition root: único lugar que conoce todas las capas. */
public class Main {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);

        var residentRepository = new InMemoryResidentRepository();
        var visitorRepository = new InMemoryVisitorRepository();

        var residentCli = new ResidentCli(
                new RegisterResidentUseCase(residentRepository),
                new ListResidentsUseCase(residentRepository),
                scanner);
        var visitorCli = new VisitorCli(
                new RegisterVisitorUseCase(visitorRepository),
                new ListVisitorsUseCase(visitorRepository),
                scanner);

        new MainMenuCli(residentCli, visitorCli, scanner).run();
    }
}
