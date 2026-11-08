package com.hwresident.presentation.cli;

import com.hwresident.application.usecase.ListVisitorsUseCase;
import com.hwresident.application.usecase.RegisterVisitorUseCase;
import com.hwresident.domain.exception.DomainException;
import java.util.Scanner;

public class VisitorCli {
    private final RegisterVisitorUseCase register;
    private final ListVisitorsUseCase list;
    private final Scanner in;

    public VisitorCli(RegisterVisitorUseCase register, ListVisitorsUseCase list, Scanner in) {
        this.register = register;
        this.list = list;
        this.in = in;
    }

    public void run() {
        while (true) {
            System.out.println("\n1) Registrar visitante  2) Listar visitantes  0) Volver");
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
                        register.execute(name, doc);
                        System.out.println("Visitante registrado.");
                    }
                    case "2" -> list.execute().forEach(v ->
                            System.out.printf("- %s (%s)%n", v.fullName(), v.documentNumber()));
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
