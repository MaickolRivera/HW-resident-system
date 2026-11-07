package com.hwresident.application.usecase;

import com.hwresident.domain.exception.DomainException;
import com.hwresident.domain.model.Visitor;
import com.hwresident.domain.repository.VisitorRepository;

public class RegisterVisitorUseCase {
    private final VisitorRepository visitors;

    public RegisterVisitorUseCase(VisitorRepository visitors) {
        this.visitors = visitors;
    }

    public Visitor execute(String fullName, String documentNumber) {
        if (visitors.findByDocumentNumber(documentNumber).isPresent()) {
            throw new DomainException("Ya existe un visitante con ese documento");
        }
        return visitors.save(Visitor.create(fullName, documentNumber));
    }
}
