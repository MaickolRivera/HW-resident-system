package com.hwresident;

import com.hwresident.application.usecase.ListResidentsUseCase;
import com.hwresident.application.usecase.RegisterResidentUseCase;
import com.hwresident.infrastructure.persistence.InMemoryResidentRepository;
import com.hwresident.presentation.cli.ResidentCli;

/** Composition root: único lugar que conoce todas las capas. */
public class Main {
    public static void main(String[] args) {
        var residents = new InMemoryResidentRepository();
        new ResidentCli(
                new RegisterResidentUseCase(residents),
                new ListResidentsUseCase(residents)
        ).run();
    }
}
