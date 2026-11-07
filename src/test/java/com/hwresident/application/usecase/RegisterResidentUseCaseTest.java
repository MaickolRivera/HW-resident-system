package com.hwresident.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hwresident.domain.exception.DomainException;
import com.hwresident.infrastructure.persistence.InMemoryResidentRepository;
import org.junit.jupiter.api.Test;

class RegisterResidentUseCaseTest {

    @Test
    void registraResidente() {
        var repo = new InMemoryResidentRepository();
        var resident = new RegisterResidentUseCase(repo).execute("Ana Pérez", "123456", "Torre 1 - 101");
        assertEquals("Ana Pérez", resident.fullName());
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void rechazaDocumentoDuplicado() {
        var useCase = new RegisterResidentUseCase(new InMemoryResidentRepository());
        useCase.execute("Ana Pérez", "123456", "Torre 1 - 101");
        assertThrows(DomainException.class, () -> useCase.execute("Luis Gómez", "123456", "Torre 2 - 202"));
    }
}
