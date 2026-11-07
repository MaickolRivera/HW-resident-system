package com.hwresident.domain.repository;

import com.hwresident.domain.model.Visitor;
import java.util.List;
import java.util.Optional;

public interface VisitorRepository {
    Visitor save(Visitor visitor);
    Optional<Visitor> findByDocumentNumber(String documentNumber);
    List<Visitor> findAll();
}
