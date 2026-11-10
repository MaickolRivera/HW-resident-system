package com.hwresident.application.usecase;

import com.hwresident.domain.model.Apartment;
import com.hwresident.domain.repository.ApartmentRepository;
import java.util.Comparator;
import java.util.List;

public class ListApartmentsUseCase {
    private final ApartmentRepository apartments;

    public ListApartmentsUseCase(ApartmentRepository apartments) {
        this.apartments = apartments;
    }

    /** Ordenados por torre y número. */
    public List<Apartment> execute() {
        return apartments.findAll().stream()
                .sorted(Comparator.comparing(Apartment::tower).thenComparing(Apartment::number))
                .toList();
    }
}
