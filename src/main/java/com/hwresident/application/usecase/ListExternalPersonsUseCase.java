package com.hwresident.application.usecase;

import com.hwresident.domain.model.ExternalPerson;
import com.hwresident.domain.repository.PersonRepository;
import java.util.List;

/** Visitantes y prestadores de servicio. */
public class ListExternalPersonsUseCase {
    private final PersonRepository persons;

    public ListExternalPersonsUseCase(PersonRepository persons) {
        this.persons = persons;
    }

    public List<ExternalPerson> execute() {
        return persons.findAllOfType(ExternalPerson.class);
    }
}
