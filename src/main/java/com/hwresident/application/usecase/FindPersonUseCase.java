package com.hwresident.application.usecase;

import com.hwresident.domain.model.Person;
import com.hwresident.domain.repository.PersonRepository;
import java.util.Optional;

/** Portería: ¿quién es el dueño de este documento? */
public class FindPersonUseCase {
    private final PersonRepository persons;

    public FindPersonUseCase(PersonRepository persons) {
        this.persons = persons;
    }

    public Optional<Person> execute(String documentNumber) {
        return persons.findByDocumentNumber(documentNumber);
    }
}
