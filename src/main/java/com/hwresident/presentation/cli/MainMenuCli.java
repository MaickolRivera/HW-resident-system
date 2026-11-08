package com.hwresident.presentation.cli;

import java.util.Scanner;

public class MainMenuCli {
    private final ResidentCli residents;
    private final VisitorCli visitors;
    private final Scanner in;

    public MainMenuCli(ResidentCli residents, VisitorCli visitors, Scanner in) {
        this.residents = residents;
        this.visitors = visitors;
        this.in = in;
    }

    public void run() {
        while (true) {
            System.out.println("\n=== HW Resident System ===");
            System.out.println("1) Residentes  2) Visitantes  0) Salir");
            System.out.print("> ");
            if (!in.hasNextLine()) return;
            switch (in.nextLine().trim()) {
                case "1" -> residents.run();
                case "2" -> visitors.run();
                case "0" -> {
                    return;
                }
                default -> System.out.println("Opción inválida.");
            }
        }
    }
}
