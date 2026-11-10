package com.hwresident;

import com.hwresident.application.usecase.GrantAccessPermissionUseCase;
import com.hwresident.application.usecase.ListPeopleInsideUseCase;
import com.hwresident.application.usecase.RegisterApartmentUseCase;
import com.hwresident.application.usecase.RegisterEntryUseCase;
import com.hwresident.application.usecase.RegisterExitUseCase;
import com.hwresident.application.usecase.RegisterGuardUseCase;
import com.hwresident.application.usecase.RegisterResidentUseCase;
import com.hwresident.application.usecase.RegisterServiceProviderUseCase;
import com.hwresident.application.usecase.RegisterVisitorUseCase;
import com.hwresident.application.usecase.ResolveAuthorizationUseCase;
import com.hwresident.application.usecase.SearchAccessHistoryUseCase;
import com.hwresident.domain.model.Apartment;
import com.hwresident.domain.model.Guard;
import com.hwresident.domain.model.Resident;
import com.hwresident.domain.model.ServiceKind;
import com.hwresident.domain.model.ServiceProvider;
import com.hwresident.domain.model.Visitor;
import com.hwresident.domain.repository.AccessPermissionRepository;
import com.hwresident.domain.repository.AccessRecordRepository;
import com.hwresident.domain.repository.ApartmentRepository;
import com.hwresident.domain.repository.PersonRepository;
import com.hwresident.infrastructure.persistence.InMemoryAccessPermissionRepository;
import com.hwresident.infrastructure.persistence.InMemoryAccessRecordRepository;
import com.hwresident.infrastructure.persistence.InMemoryApartmentRepository;
import com.hwresident.infrastructure.persistence.InMemoryPersonRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Escenario de prueba común: dos apartamentos, una residente, un guarda,
 * un visitante y una domiciliaria, con un reloj que se puede adelantar.
 */
public final class TestData {
    public static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 9, 12, 0);

    public final MutableClock clock = new MutableClock(NOW);
    public final PersonRepository persons;
    public final ApartmentRepository apartments;
    public final AccessPermissionRepository permissions;
    public final AccessRecordRepository records;

    public final Apartment t1101;
    public final Apartment t2302;
    public final Resident ana;
    public final Guard guard;
    public final Visitor carlos;
    public final ServiceProvider diana;

    public TestData() {
        this(new InMemoryPersonRepository(), new InMemoryApartmentRepository(),
                new InMemoryAccessPermissionRepository(), new InMemoryAccessRecordRepository());
    }

    public TestData(PersonRepository persons, ApartmentRepository apartments,
                    AccessPermissionRepository permissions, AccessRecordRepository records) {
        this.persons = persons;
        this.apartments = apartments;
        this.permissions = permissions;
        this.records = records;
        var registerApartment = new RegisterApartmentUseCase(apartments);
        t1101 = registerApartment.execute("1", "101");
        t2302 = registerApartment.execute("2", "302");
        ana = new RegisterResidentUseCase(persons, apartments).execute("Ana Pérez", "123", t1101.id());
        guard = new RegisterGuardUseCase(persons).execute("Pedro Castillo", "500");
        carlos = new RegisterVisitorUseCase(persons).execute("Carlos Ruiz", "987");
        diana = new RegisterServiceProviderUseCase(persons)
                .execute("Diana Torres", "300", ServiceKind.DELIVERY, "Rappi", "Domicilios");
    }

    public GrantAccessPermissionUseCase grant() { return new GrantAccessPermissionUseCase(persons, permissions); }
    public RegisterEntryUseCase registerEntry() { return new RegisterEntryUseCase(persons, apartments, permissions, records, clock); }
    public ResolveAuthorizationUseCase resolve() { return new ResolveAuthorizationUseCase(records, clock); }
    public RegisterExitUseCase registerExit() { return new RegisterExitUseCase(persons, records, clock); }
    public ListPeopleInsideUseCase listInside() { return new ListPeopleInsideUseCase(records); }
    public SearchAccessHistoryUseCase history() { return new SearchAccessHistoryUseCase(records); }

    /** Reloj fijo que se puede adelantar en las pruebas. */
    public static final class MutableClock extends Clock {
        private Instant instant;

        MutableClock(LocalDateTime start) {
            this.instant = start.toInstant(ZoneOffset.UTC);
        }

        public void advance(Duration duration) {
            instant = instant.plus(duration);
        }

        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return instant; }
    }
}
