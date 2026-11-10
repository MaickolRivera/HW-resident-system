package com.hwresident.presentation.cli;

import com.hwresident.application.usecase.ListApartmentsUseCase;
import com.hwresident.application.usecase.ListResidentsUseCase;
import com.hwresident.application.usecase.RegisterResidentUseCase;
import com.hwresident.domain.exception.DomainException;
import java.util.Scanner;

public class ResidentCli {
    private final RegisterResidentUseCase register;
    private final ListResidentsUseCase list;
    private final ListApartmentsUseCase apartments;
    private final Scanner in;

    public ResidentCli(RegisterResidentUseCase register, ListResidentsUseCase list,
                       ListApartmentsUseCase apartments, Scanner in) {
        this.register = register;
        this.list = list;
        this.apartments = apartments;
        this.in = in;
    }

    public void run() {
        while (true) {
            System.out.println("\n1) Registrar residente  2) Listar residentes  0) Volver");
            System.out.print("> ");
            if (!in.hasNextLine()) return;
            String option = in.nextLine().trim();
            try {
                switch (option) {
                    case "1" -> {
                        var options = apartments.execute();
                        if (options.isEmpty()) {
                            System.out.println("No hay apartamentos registrados (regístrelos en la GUI).");
                            continue;
                        }
                        System.out.print("Nombre: ");
                        String name = in.nextLine();
                        System.out.print("Documento: ");
                        String doc = in.nextLine();
                        for (int i = 0; i < options.size(); i++) {
                            System.out.printf("  %d) %s%n", i + 1, options.get(i).label());
                        }
                        System.out.print("Apartamento (número de la lista): ");
                        int index;
                        try {
                            index = Integer.parseInt(in.nextLine().trim()) - 1;
                        } catch (NumberFormatException e) {
                            index = -1;
                        }
                        if (index < 0 || index >= options.size()) {
                            System.out.println("Opción de apartamento inválida.");
                            continue;
                        }
                        register.execute(name, doc, options.get(index).id());
                        System.out.println("Residente registrado.");
                    }
                    case "2" -> list.execute().forEach(r ->
                            System.out.printf("- %s (%s) %s%n", r.fullName(), r.documentNumber(), r.apartment().label()));
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (DomainException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
