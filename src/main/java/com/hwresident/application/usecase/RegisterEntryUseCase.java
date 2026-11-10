package com.hwresident.application.usecase;

import com.hwresident.application.usecase.EntryResult.Outcome;
import com.hwresident.domain.exception.DomainException;
import com.hwresident.domain.model.AccessRecord;
import com.hwresident.domain.model.Apartment;
import com.hwresident.domain.model.ExternalPerson;
import com.hwresident.domain.model.Guard;
import com.hwresident.domain.model.Person;
import com.hwresident.domain.model.Resident;
import com.hwresident.domain.model.ServiceProvider;
import com.hwresident.domain.model.Vehicle;
import com.hwresident.domain.model.VehicleType;
import com.hwresident.domain.model.Visitor;
import com.hwresident.domain.repository.AccessPermissionRepository;
import com.hwresident.domain.repository.AccessRecordRepository;
import com.hwresident.domain.repository.ApartmentRepository;
import com.hwresident.domain.repository.PersonRepository;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Registrar ingreso (flujo de portería):
 * <ol>
 *   <li>Residente: se registra el ingreso directo a su apartamento.</li>
 *   <li>Externo con permiso vigente para el apartamento destino: ingreso directo.</li>
 *   <li>Externo sin permiso (o no registrado, que se registra en ese momento): el registro
 *       queda EN_ESPERA hasta que el residente autorice o rechace
 *       ({@link ResolveAuthorizationUseCase}).</li>
 * </ol>
 * Nadie puede tener dos ingresos abiertos a la vez.
 */
public class RegisterEntryUseCase {
    private final PersonRepository persons;
    private final ApartmentRepository apartments;
    private final AccessPermissionRepository permissions;
    private final AccessRecordRepository records;
    private final Clock clock;

    public RegisterEntryUseCase(PersonRepository persons, ApartmentRepository apartments,
                                AccessPermissionRepository permissions, AccessRecordRepository records,
                                Clock clock) {
        this.persons = persons;
        this.apartments = apartments;
        this.permissions = permissions;
        this.records = records;
        this.clock = clock;
    }

    public EntryResult execute(EntryRequest request) {
        var now = LocalDateTime.now(clock).withSecond(0).withNano(0);
        var guard = persons.findByDocumentNumber(trim(request.guardDocument()), Guard.class)
                .orElseThrow(() -> new DomainException("Seleccione el guarda de turno"));
        var document = trim(request.documentNumber());
        if (document.isEmpty()) throw new DomainException("Ingrese el documento de quien llega");
        var vehicle = vehicleOf(request);

        var existing = persons.findByDocumentNumber(document);
        if (existing.isPresent() && existing.get() instanceof Guard) {
            throw new DomainException("El documento pertenece a un guarda; los guardas no se registran en la minuta");
        }
        existing.ifPresent(this::ensureNoOpenEntry);

        if (existing.isPresent() && existing.get() instanceof Resident resident) {
            var record = AccessRecord.request(resident, resident.apartment(), request.reason(), vehicle,
                    request.correspondence(), guard, now);
            record.admit(now);
            return new EntryResult(records.save(record), Outcome.RESIDENT_ENTERED);
        }

        // Externo: el destino es obligatorio y se valida antes de registrar a alguien nuevo.
        Apartment destination = apartments.findById(request.destinationId())
                .orElseThrow(() -> new DomainException("Seleccione el apartamento destino"));
        ExternalPerson external = existing.map(ExternalPerson.class::cast)
                .orElseGet(() -> registerNewExternal(request, document));

        var record = AccessRecord.request(external, destination, request.reason(), vehicle,
                request.correspondence(), guard, now);
        boolean preAuthorized = permissions.findByExternalId(external.id()).stream()
                .anyMatch(p -> p.allowsEntryTo(destination.id(), now));
        if (preAuthorized) {
            record.admit(now);
            return new EntryResult(records.save(record), Outcome.PRE_AUTHORIZED);
        }
        return new EntryResult(records.save(record), Outcome.AWAITING_AUTHORIZATION);
    }

    private void ensureNoOpenEntry(Person person) {
        records.findOpenByPerson(person.id()).ifPresent(open -> {
            throw new DomainException(switch (open.status()) {
                case INSIDE -> person.fullName() + " ya se encuentra dentro del conjunto (registre su salida primero)";
                default -> person.fullName() + " ya tiene una solicitud de ingreso en espera";
            });
        });
    }

    private ExternalPerson registerNewExternal(EntryRequest request, String document) {
        if (request.fullName() == null || request.fullName().isBlank()) {
            throw new DomainException("Persona no registrada: ingrese su nombre para registrarla");
        }
        ExternalPerson person = request.serviceKind() == null
                ? Visitor.create(request.fullName(), document)
                : ServiceProvider.create(request.fullName(), document, request.serviceKind(),
                        request.company(), request.labor());
        return persons.save(person);
    }

    private static Vehicle vehicleOf(EntryRequest request) {
        if (request.vehiclePlate() == null || request.vehiclePlate().isBlank()) return null;
        var type = request.vehicleType() == null ? VehicleType.CAR : request.vehicleType();
        return new Vehicle(request.vehiclePlate(), type);
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
