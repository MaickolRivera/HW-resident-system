package com.hwresident.application.usecase;

import com.hwresident.domain.model.ServiceKind;
import com.hwresident.domain.model.ServiceProvider;
import com.hwresident.domain.repository.PersonRepository;

/** Registra domiciliarios, contratistas y personal de servicio. */
public class RegisterServiceProviderUseCase {
    private final PersonRepository persons;

    public RegisterServiceProviderUseCase(PersonRepository persons) {
        this.persons = persons;
    }

    public ServiceProvider execute(String fullName, String documentNumber, ServiceKind kind,
                                   String company, String labor) {
        var doc = Documents.ensureAvailable(persons, documentNumber);
        return persons.save(ServiceProvider.create(fullName, doc, kind, company, labor));
    }
}
