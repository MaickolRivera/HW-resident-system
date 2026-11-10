package com.hwresident.application.usecase;

import com.hwresident.domain.exception.DomainException;
import com.hwresident.domain.model.Apartment;
import com.hwresident.domain.repository.ApartmentRepository;

public class RegisterApartmentUseCase {
    private final ApartmentRepository apartments;

    public RegisterApartmentUseCase(ApartmentRepository apartments) {
        this.apartments = apartments;
    }

    public Apartment execute(String tower, String number) {
        var apartment = Apartment.create(tower, number); // valida campos obligatorios
        if (apartments.findByTowerAndNumber(apartment.tower(), apartment.number()).isPresent()) {
            throw new DomainException("Ya existe el apartamento " + apartment.label());
        }
        return apartments.save(apartment);
    }
}
