package com.hwresident.application.usecase;

import com.hwresident.domain.exception.DomainException;
import com.hwresident.domain.model.Resident;
import com.hwresident.domain.repository.ApartmentRepository;
import com.hwresident.domain.repository.PersonRepository;
import java.util.UUID;

public class RegisterResidentUseCase {
    private final PersonRepository persons;
    private final ApartmentRepository apartments;

    public RegisterResidentUseCase(PersonRepository persons, ApartmentRepository apartments) {
        this.persons = persons;
        this.apartments = apartments;
    }

    public Resident execute(String fullName, String documentNumber, UUID apartmentId) {
        var apartment = apartments.findById(apartmentId)
                .orElseThrow(() -> new DomainException("Seleccione un apartamento (regístrelo primero si no existe)"));
        var doc = Documents.ensureAvailable(persons, documentNumber);
        return persons.save(Resident.create(fullName, doc, apartment));
    }
}
