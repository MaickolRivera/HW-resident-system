package com.hwresident.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hwresident.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

/** Jerarquía de actores: Person -> Resident / Guard / ExternalPerson -> Visitor / ServiceProvider. */
class PersonTest {

    private final Apartment apartment = Apartment.create("1", "101");

    @Test
    void todosLosActoresSonPersonas() {
        assertInstanceOf(Person.class, Resident.create("Ana", "1", apartment));
        assertInstanceOf(Person.class, Guard.create("Pedro", "2"));
        assertInstanceOf(ExternalPerson.class, Visitor.create("Carlos", "3"));
        assertInstanceOf(ExternalPerson.class,
                ServiceProvider.create("Diana", "4", ServiceKind.CONTRACTOR, "Pinturas JR", "Pintura"));
    }

    @Test
    void validacionesComunesEnLaSuperclase() {
        var e = assertThrows(DomainException.class, () -> Visitor.create("Carlos", "abc"));
        assertEquals("El documento debe ser numérico", e.getMessage());
        assertThrows(DomainException.class, () -> Guard.create("  ", "1"));
    }

    @Test
    void prestadorExigeEmpresaYLabor() {
        assertThrows(DomainException.class,
                () -> ServiceProvider.create("Diana", "4", ServiceKind.DELIVERY, "", "Domicilios"));
        assertThrows(DomainException.class,
                () -> ServiceProvider.create("Diana", "4", ServiceKind.DELIVERY, "Rappi", " "));
    }

    @Test
    void tipoDelPrestadorSaleDeSuLabor() {
        assertEquals("Domiciliario",
                ServiceProvider.create("Diana", "4", ServiceKind.DELIVERY, "Rappi", "Domicilios").typeLabel());
        assertEquals("Residente", Resident.create("Ana", "1", apartment).typeLabel());
    }

    @Test
    void residenteExigeApartamento() {
        assertThrows(DomainException.class, () -> Resident.create("Ana", "1", null));
    }

    @Test
    void placaSeNormaliza() {
        assertEquals("ABC123", new Vehicle(" abc123 ", VehicleType.CAR).plate());
    }
}
