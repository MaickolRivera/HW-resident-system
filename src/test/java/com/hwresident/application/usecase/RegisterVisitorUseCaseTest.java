package com.hwresident.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hwresident.domain.exception.DomainException;
import com.hwresident.domain.model.Visitor;
import com.hwresident.domain.repository.VisitorRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class RegisterVisitorUseCaseTest {

    private static class FakeVisitorRepository implements VisitorRepository {
        private final List<Visitor> store = new ArrayList<>();

        @Override
        public Visitor save(Visitor visitor) {
            store.add(visitor);
            return visitor;
        }

        @Override
        public Optional<Visitor> findByDocumentNumber(String documentNumber) {
            return store.stream().filter(v -> v.documentNumber().equals(documentNumber)).findFirst();
        }

        @Override
        public List<Visitor> findAll() {
            return List.copyOf(store);
        }
    }

    @Test
    void registraVisitante() {
        var repo = new FakeVisitorRepository();
        var visitor = new RegisterVisitorUseCase(repo).execute("Carlos Ruiz", "987654");
        assertEquals("Carlos Ruiz", visitor.fullName());
        assertEquals(1, new ListVisitorsUseCase(repo).execute().size());
    }

    @Test
    void rechazaDocumentoDuplicado() {
        var useCase = new RegisterVisitorUseCase(new FakeVisitorRepository());
        useCase.execute("Carlos Ruiz", "987654");
        assertThrows(DomainException.class, () -> useCase.execute("Otra Persona", "987654"));
    }

    @Test
    void rechazaDocumentoNoNumerico() {
        var useCase = new RegisterVisitorUseCase(new FakeVisitorRepository());
        assertThrows(DomainException.class, () -> useCase.execute("Carlos Ruiz", "abc"));
    }
}
