package com.hwresident.application.usecase;

import com.hwresident.domain.model.Resident;
import com.hwresident.domain.repository.PersonRepository;
import java.util.List;

public class ListResidentsUseCase {
    private final PersonRepository persons;

    public ListResidentsUseCase(PersonRepository persons) {
        this.persons = persons;
    }

    public List<Resident> execute() {
        return persons.findAllOfType(Resident.class);
    }
}
