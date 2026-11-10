package com.hwresident.domain.model;

import com.hwresident.domain.exception.DomainException;
import java.util.UUID;

/** Residente: vive en un apartamento y entra al conjunto sin necesitar autorización. */
public class Resident extends Person {
    private static final long serialVersionUID = 1L;

    private final Apartment apartment;

    public Resident(UUID id, String fullName, String documentNumber, Apartment apartment) {
        super(id, fullName, documentNumber);
        if (apartment == null) throw new DomainException("El apartamento es obligatorio");
        this.apartment = apartment;
    }

    public static Resident create(String fullName, String documentNumber, Apartment apartment) {
        return new Resident(UUID.randomUUID(), fullName, documentNumber, apartment);
    }

    public Apartment apartment() { return apartment; }

    @Override
    public String typeLabel() { return "Residente"; }
}
