package com.hwresident.infrastructure.seed;

import com.hwresident.application.usecase.GrantAccessPermissionUseCase;
import com.hwresident.application.usecase.RegisterApartmentUseCase;
import com.hwresident.application.usecase.RegisterGuardUseCase;
import com.hwresident.application.usecase.RegisterResidentUseCase;
import com.hwresident.application.usecase.RegisterServiceProviderUseCase;
import com.hwresident.application.usecase.RegisterVisitorUseCase;
import com.hwresident.domain.model.AccessRecord;
import com.hwresident.domain.model.ServiceKind;
import com.hwresident.domain.model.Vehicle;
import com.hwresident.domain.model.VehicleType;
import com.hwresident.domain.repository.AccessPermissionRepository;
import com.hwresident.domain.repository.AccessRecordRepository;
import com.hwresident.domain.repository.ApartmentRepository;
import com.hwresident.domain.repository.PersonRepository;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Datos de ejemplo para la demostración: apartamentos, residentes, guardas,
 * externos, un permiso vigente y algunos movimientos en la minuta.
 * Solo se cargan cuando el almacenamiento está vacío.
 */
public final class DemoData {
    private DemoData() {}

    public static void load(PersonRepository persons, ApartmentRepository apartments,
                            AccessPermissionRepository permissions, AccessRecordRepository records, Clock clock) {
        var registerApartment = new RegisterApartmentUseCase(apartments);
        var t1101 = registerApartment.execute("1", "101");
        var t1102 = registerApartment.execute("1", "102");
        registerApartment.execute("1", "201");
        var t2301 = registerApartment.execute("2", "301");
        var t2302 = registerApartment.execute("2", "302");
        registerApartment.execute("3", "401");

        var registerResident = new RegisterResidentUseCase(persons, apartments);
        registerResident.execute("Ana Pérez", "1001", t1101.id());
        registerResident.execute("Luis Gómez", "1002", t1102.id());
        registerResident.execute("María Rodríguez", "1003", t2301.id());
        registerResident.execute("Jorge Martínez", "1004", t2302.id());
        registerResident.execute("Sofía Martínez", "1005", t2302.id());

        var registerGuard = new RegisterGuardUseCase(persons);
        var pedro = registerGuard.execute("Pedro Castillo", "5001");
        registerGuard.execute("Sandra López", "5002");

        var registerVisitor = new RegisterVisitorUseCase(persons);
        registerVisitor.execute("Carlos Ruiz", "2001");
        registerVisitor.execute("Laura Díaz", "2002");

        var registerProvider = new RegisterServiceProviderUseCase(persons);
        var diana = registerProvider.execute("Diana Torres", "3001", ServiceKind.SERVICE_STAFF,
                "Aseo Total S.A.S.", "Aseo del apartamento");
        var andres = registerProvider.execute("Andrés Mora", "3002", ServiceKind.DELIVERY,
                "Rappi", "Entrega de domicilios");
        registerProvider.execute("Felipe Rojas", "3003", ServiceKind.CONTRACTOR,
                "Pinturas JR", "Pintura de fachada");

        var now = LocalDateTime.now(clock).withSecond(0).withNano(0);
        // Carlos Ruiz (2001) tiene permiso de Ana Pérez para hoy: entra directo a la Torre 1 - 101.
        new GrantAccessPermissionUseCase(persons, permissions)
                .execute("1001", "2001", now.minusHours(1), now.plusHours(10));

        // Movimientos previos: Diana sigue dentro; Andrés ya salió.
        var inside = AccessRecord.request(diana, t2302, "Aseo semanal", null, null, pedro, now.minusHours(3));
        inside.admit(now.minusHours(3));
        records.save(inside);

        var done = AccessRecord.request(andres, t1102, "Domicilio", new Vehicle("abc12d", VehicleType.MOTORCYCLE),
                "Pedido de comida", pedro, now.minusHours(2));
        done.admit(now.minusHours(2));
        done.registerExit(now.minusHours(2).plusMinutes(10), pedro);
        records.save(done);
    }
}
