package com.hwresident.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hwresident.domain.model.Visitor;
import org.junit.jupiter.api.Test;

class InMemoryVisitorRepositoryTest {

    @Test
    void guardaYBuscaPorDocumento() {
        var repo = new InMemoryVisitorRepository();
        var saved = repo.save(Visitor.create("Carlos Ruiz", "987654"));

        var found = repo.findByDocumentNumber("987654");

        assertTrue(found.isPresent());
        assertEquals(saved.id(), found.get().id());
    }

    @Test
    void buscaDocumentoInexistente() {
        assertTrue(new InMemoryVisitorRepository().findByDocumentNumber("1").isEmpty());
    }

    @Test
    void listaTodos() {
        var repo = new InMemoryVisitorRepository();
        repo.save(Visitor.create("A", "1"));
        repo.save(Visitor.create("B", "2"));
        assertEquals(2, repo.findAll().size());
    }
}
