package com.hwresident.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hwresident.domain.exception.DomainException;
import com.hwresident.infrastructure.persistence.InMemoryApartmentRepository;
import com.hwresident.infrastructure.persistence.InMemoryPersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegisterResidentUseCaseTest {

    private InMemoryPersonRepository persons;
    private InMemoryApartmentRepository apartments;
    private RegisterResidentUseCase useCase;

    @BeforeEach
    void setUp() {
        persons = new InMemoryPersonRepository();
        apartments = new InMemoryApartmentRepository();
        useCase = new RegisterResidentUseCase(persons, apartments);
    }

    @Test
    void registraResidenteEnSuApartamento() {
        var apartment = new RegisterApartmentUseCase(apartments).execute("1", "101");
        var resident = useCase.execute("Ana Pérez", "123456", apartment.id());
        assertEquals("Ana Pérez", resident.fullName());
        assertEquals("Torre 1 - 101", resident.apartment().label());
        assertEquals(1, new ListResidentsUseCase(persons).execute().size());
    }

    @Test
    void rechazaDocumentoDuplicadoAunqueSeaDeOtroTipoDePersona() {
        var apartment = new RegisterApartmentUseCase(apartments).execute("1", "101");
        new RegisterVisitorUseCase(persons).execute("Carlos Ruiz", "123456");
        var e = assertThrows(DomainException.class, () -> useCase.execute("Luis Gómez", "123456", apartment.id()));
        assertEquals("Ya existe una persona con ese documento (visitante: Carlos Ruiz)", e.getMessage());
    }

    @Test
    void exigeApartamentoExistente() {
        assertThrows(DomainException.class, () -> useCase.execute("Ana", "1", null));
    }

    @Test
    void noRegistraElMismoApartamentoDosVeces() {
        var register = new RegisterApartmentUseCase(apartments);
        register.execute("1", "101");
        assertThrows(DomainException.class, () -> register.execute(" 1 ", "101"));
    }
}
