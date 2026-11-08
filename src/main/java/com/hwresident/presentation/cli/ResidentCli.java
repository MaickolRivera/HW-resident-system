package com.hwresident.presentation.cli;

import com.hwresident.application.usecase.ListResidentsUseCase;
import com.hwresident.application.usecase.RegisterResidentUseCase;
import com.hwresident.domain.exception.DomainException;
import java.util.Scanner;

public class ResidentCli {
    private final RegisterResidentUseCase register;
    private final ListResidentsUseCase list;
    private final Scanner in;

    public ResidentCli(RegisterResidentUseCase register, ListResidentsUseCase list, Scanner in) {
        this.register = register;
        this.list = list;
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
                        System.out.print("Nombre: ");
                        String name = in.nextLine();
                        System.out.print("Documento: ");
                        String doc = in.nextLine();
                        System.out.print("Unidad: ");
                        String unit = in.nextLine();
                        register.execute(name, doc, unit);
                        System.out.println("Residente registrado.");
                    }
                    case "2" -> list.execute().forEach(r ->
                            System.out.printf("- %s (%s) %s%n", r.fullName(), r.documentNumber(), r.unit()));
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
