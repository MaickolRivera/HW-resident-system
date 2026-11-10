package com.hwresident;

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
import com.hwresident.domain.repository.AccessPermissionRepository;
import com.hwresident.domain.repository.AccessRecordRepository;
import com.hwresident.domain.repository.ApartmentRepository;
import com.hwresident.domain.repository.PersonRepository;
import com.hwresident.infrastructure.persistence.FileDatabase;
import com.hwresident.infrastructure.persistence.InMemoryAccessPermissionRepository;
import com.hwresident.infrastructure.persistence.InMemoryAccessRecordRepository;
import com.hwresident.infrastructure.persistence.InMemoryApartmentRepository;
import com.hwresident.infrastructure.persistence.InMemoryPersonRepository;
import com.hwresident.infrastructure.seed.DemoData;
import com.hwresident.presentation.cli.MainMenuCli;
import com.hwresident.presentation.cli.ResidentCli;
import com.hwresident.presentation.cli.VisitorCli;
import com.hwresident.presentation.gui.GuiUseCases;
import com.hwresident.presentation.gui.swing.SwingApp;
import java.awt.GraphicsEnvironment;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Composition root: único lugar que conoce todas las capas.
 *
 * <p>Opciones:
 * <ul>
 *   <li>{@code --cli}: modo consola (también se usa si no hay entorno gráfico).</li>
 *   <li>{@code --memoria}: no guarda en disco; todo se pierde al cerrar.</li>
 *   <li>{@code --sin-demo}: no precarga los datos de ejemplo.</li>
 * </ul>
 * Por defecto los datos se guardan en {@code datos/hw-resident.dat} (carpeta de trabajo).
 */
public class Main {
    static final Path DATA_FILE = Path.of("datos", "hw-resident.dat");

    public static void main(String[] args) {
        var options = Arrays.asList(args);
        var clock = Clock.systemDefaultZone();

        PersonRepository persons;
        ApartmentRepository apartments;
        AccessPermissionRepository permissions;
        AccessRecordRepository records;
        boolean empty;
        if (options.contains("--memoria")) {
            persons = new InMemoryPersonRepository();
            apartments = new InMemoryApartmentRepository();
            permissions = new InMemoryAccessPermissionRepository();
            records = new InMemoryAccessRecordRepository();
            empty = true;
        } else {
            var db = FileDatabase.open(DATA_FILE);
            persons = db.persons();
            apartments = db.apartments();
            permissions = db.permissions();
            records = db.records();
            empty = db.isEmpty();
            System.out.println("Datos guardados en " + db.file().toAbsolutePath());
        }
        if (empty && !options.contains("--sin-demo")) {
            DemoData.load(persons, apartments, permissions, records, clock);
        }

        var registerResident = new RegisterResidentUseCase(persons, apartments);
        var listResidents = new ListResidentsUseCase(persons);
        var listApartments = new ListApartmentsUseCase(apartments);
        var registerVisitor = new RegisterVisitorUseCase(persons);
        var listExternals = new ListExternalPersonsUseCase(persons);

        boolean forceCli = options.contains("--cli");
        if (!forceCli && !GraphicsEnvironment.isHeadless()) {
            SwingApp.launch(new GuiUseCases(
                    new RegisterApartmentUseCase(apartments),
                    listApartments,
                    registerResident,
                    listResidents,
                    registerVisitor,
                    new RegisterServiceProviderUseCase(persons),
                    listExternals,
                    new RegisterGuardUseCase(persons),
                    new ListGuardsUseCase(persons),
                    new FindPersonUseCase(persons),
                    new GrantAccessPermissionUseCase(persons, permissions),
                    new RevokeAccessPermissionUseCase(permissions),
                    new ListAccessPermissionsUseCase(permissions),
                    new CheckAccessUseCase(persons, permissions),
                    new RegisterEntryUseCase(persons, apartments, permissions, records, clock),
                    new ResolveAuthorizationUseCase(records, clock),
                    new RegisterExitUseCase(persons, records, clock),
                    new ListPeopleInsideUseCase(records),
                    new SearchAccessHistoryUseCase(records),
                    clock));
            return;
        }

        if (!forceCli) {
            System.err.println("No hay entorno gráfico disponible; iniciando modo consola.");
        }
        var scanner = new Scanner(System.in);
        var residentCli = new ResidentCli(registerResident, listResidents, listApartments, scanner);
        var visitorCli = new VisitorCli(registerVisitor, listExternals, scanner);
        new MainMenuCli(residentCli, visitorCli, scanner).run();
    }
}
