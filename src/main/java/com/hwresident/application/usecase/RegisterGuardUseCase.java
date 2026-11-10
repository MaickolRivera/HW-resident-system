package com.hwresident.application.usecase;

import com.hwresident.domain.model.Guard;
import com.hwresident.domain.repository.PersonRepository;

public class RegisterGuardUseCase {
    private final PersonRepository persons;

    public RegisterGuardUseCase(PersonRepository persons) {
        this.persons = persons;
    }

    public Guard execute(String fullName, String documentNumber) {
        var doc = Documents.ensureAvailable(persons, documentNumber);
        return persons.save(Guard.create(fullName, doc));
    }
}
