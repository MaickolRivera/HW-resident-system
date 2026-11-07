package com.hwresident.domain.repository;

import com.hwresident.domain.model.Resident;
import java.util.List;
import java.util.Optional;

/** Puerto de salida: el dominio define el contrato, la infraestructura lo implementa. */
public interface ResidentRepository {
    Resident save(Resident resident);
    Optional<Resident> findByDocumentNumber(String documentNumber);
    List<Resident> findAll();
}
