package com.hwresident.application.usecase;

import com.hwresident.domain.model.Resident;
import com.hwresident.domain.repository.ResidentRepository;
import java.util.List;

public class ListResidentsUseCase {
    private final ResidentRepository residents;

    public ListResidentsUseCase(ResidentRepository residents) {
        this.residents = residents;
    }

    public List<Resident> execute() {
        return residents.findAll();
    }
}
