package com.hwresident.application.usecase;

import com.hwresident.domain.model.Visitor;
import com.hwresident.domain.repository.PersonRepository;

public class RegisterVisitorUseCase {
    private final PersonRepository persons;

    public RegisterVisitorUseCase(PersonRepository persons) {
        this.persons = persons;
    }

    public Visitor execute(String fullName, String documentNumber) {
        var doc = Documents.ensureAvailable(persons, documentNumber);
        return persons.save(Visitor.create(fullName, doc));
    }
}
