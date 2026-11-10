package com.hwresident.domain.model;

import com.hwresident.domain.exception.DomainException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Registro de la minuta digital: el ingreso (y luego la salida) de una persona al conjunto.
 * Las transiciones de estado se validan aquí, en el dominio.
 */
public class AccessRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Person person;
    private final Apartment destination;
    private final String reason;
    private final Vehicle vehicle;
    private final String correspondence;
    private final Guard entryGuard;
    private final LocalDateTime requestedAt;
    private AccessStatus status;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Guard exitGuard;

    private AccessRecord(UUID id, Person person, Apartment destination, String reason, Vehicle vehicle,
                         String correspondence, Guard entryGuard, LocalDateTime requestedAt) {
        if (person == null) throw new DomainException("La persona es obligatoria");
        if (destination == null) throw new DomainException("El inmueble destino es obligatorio");
        if (entryGuard == null) throw new DomainException("Seleccione el guarda de turno");
        if (requestedAt == null) throw new DomainException("La fecha y hora son obligatorias");
        this.id = id;
        this.person = person;
        this.destination = destination;
        this.reason = blankToNull(reason);
        this.vehicle = vehicle;
        this.correspondence = blankToNull(correspondence);
        this.entryGuard = entryGuard;
        this.requestedAt = requestedAt;
        this.status = AccessStatus.PENDING;
    }

    /** Crea un registro EN_ESPERA (pendiente de que el residente autorice). */
    public static AccessRecord request(Person person, Apartment destination, String reason, Vehicle vehicle,
                                       String correspondence, Guard guard, LocalDateTime at) {
        return new AccessRecord(UUID.randomUUID(), person, destination, reason, vehicle, correspondence, guard, at);
    }

    /** EN_ESPERA -> DENTRO. */
    public void admit(LocalDateTime at) {
        if (status == AccessStatus.INSIDE || status == AccessStatus.EXITED) {
            throw new DomainException("El ingreso ya fue registrado");
        }
        if (status == AccessStatus.REJECTED) {
            throw new DomainException("El ingreso fue rechazado; registre una nueva solicitud");
        }
        if (at == null || at.isBefore(requestedAt)) {
            throw new DomainException("La hora de entrada no puede ser anterior a la solicitud");
        }
        this.entryTime = at;
        this.status = AccessStatus.INSIDE;
    }

    /** EN_ESPERA -> RECHAZADO. */
    public void reject() {
        if (status != AccessStatus.PENDING) {
            throw new DomainException("Solo se puede rechazar un ingreso en espera");
        }
        this.status = AccessStatus.REJECTED;
    }

    /** DENTRO -> SALIDA_REGISTRADA. */
    public void registerExit(LocalDateTime at, Guard guard) {
        if (status == AccessStatus.EXITED) throw new DomainException("La salida ya fue registrada");
        if (status != AccessStatus.INSIDE) {
            throw new DomainException("No se puede dar salida a alguien que no ha ingresado");
        }
        if (guard == null) throw new DomainException("Seleccione el guarda de turno");
        if (at == null || at.isBefore(entryTime)) {
            throw new DomainException("La hora de salida no puede ser anterior a la de entrada");
        }
        this.exitTime = at;
        this.exitGuard = guard;
        this.status = AccessStatus.EXITED;
    }

    /** Un registro abierto (en espera o dentro) impide registrar otro ingreso de la misma persona. */
    public boolean isOpen() {
        return status == AccessStatus.PENDING || status == AccessStatus.INSIDE;
    }

    public UUID id() { return id; }
    public Person person() { return person; }
    public Apartment destination() { return destination; }
    public Optional<String> reason() { return Optional.ofNullable(reason); }
    public Optional<Vehicle> vehicle() { return Optional.ofNullable(vehicle); }
    public Optional<String> correspondence() { return Optional.ofNullable(correspondence); }
    public Guard entryGuard() { return entryGuard; }
    public LocalDateTime requestedAt() { return requestedAt; }
    public AccessStatus status() { return status; }
    public Optional<LocalDateTime> entryTime() { return Optional.ofNullable(entryTime); }
    public Optional<LocalDateTime> exitTime() { return Optional.ofNullable(exitTime); }
    public Optional<Guard> exitGuard() { return Optional.ofNullable(exitGuard); }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
