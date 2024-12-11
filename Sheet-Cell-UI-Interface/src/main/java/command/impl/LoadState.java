package command.impl;

import api.EngineOptions;
import command.api.Command;

import java.util.Scanner;

public class LoadState implements Command {
    private final EngineOptions options;

    public LoadState(EngineOptions options) {
        this.options = options;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter file path to load state from: ");
        String filePath = scanner.nextLine();

        try {
            options.loadState(filePath);
            System.out.println("State loaded successfully from: " + filePath);
        } catch (Exception e) {
            System.out.println("Error loading state from: " + filePath + "\n" + e.getMessage());
        }
    }
}
