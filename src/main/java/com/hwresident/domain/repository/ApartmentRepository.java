package com.hwresident.domain.repository;

import com.hwresident.domain.model.Apartment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApartmentRepository {
    Apartment save(Apartment apartment);
    Optional<Apartment> findById(UUID id);
    List<Apartment> findAll();

    default Optional<Apartment> findByTowerAndNumber(String tower, String number) {
        return findAll().stream().filter(a -> a.isSameUnit(tower, number)).findFirst();
    }
}
