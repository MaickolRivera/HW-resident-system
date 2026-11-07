package com.hwresident.application.usecase;

import com.hwresident.domain.exception.DomainException;
import com.hwresident.domain.model.Resident;
import com.hwresident.domain.repository.ResidentRepository;

public class RegisterResidentUseCase {
    private final ResidentRepository residents;

    public RegisterResidentUseCase(ResidentRepository residents) {
        this.residents = residents;
    }

    public Resident execute(String fullName, String documentNumber, String unit) {
        if (residents.findByDocumentNumber(documentNumber).isPresent()) {
            throw new DomainException("Ya existe un residente con ese documento");
        }
        return residents.save(Resident.create(fullName, documentNumber, unit));
    }
}
