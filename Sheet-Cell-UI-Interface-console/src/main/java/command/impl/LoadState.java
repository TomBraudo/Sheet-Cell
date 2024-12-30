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
        System.out.println("Enter file path to load state from, or '1' to return to the main menu: ");
        String filePath = scanner.nextLine();
        if (filePath.equals("1")) {
            return;
        }

        try {
            options.loadState(filePath);
            System.out.println("State loaded successfully from: " + filePath);
        } catch (Exception e) {
            System.out.println("Error loading state from: " + filePath + "\n" + e.getMessage());
        }
    }
}
