package com.hwresident.application.usecase;

import com.hwresident.domain.model.Guard;
import com.hwresident.domain.repository.PersonRepository;
import java.util.List;

public class ListGuardsUseCase {
    private final PersonRepository persons;

    public ListGuardsUseCase(PersonRepository persons) {
        this.persons = persons;
    }

    public List<Guard> execute() {
        return persons.findAllOfType(Guard.class);
    }
}
