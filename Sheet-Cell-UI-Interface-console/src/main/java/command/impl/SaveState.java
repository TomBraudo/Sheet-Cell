package command.impl;

import api.EngineOptions;
import command.api.Command;

import java.util.Scanner;

public class SaveState implements Command {
    private final EngineOptions options;

    public SaveState(EngineOptions options) {
        this.options = options;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file path to save state to, or '1' to return to the main menu:");
        String filePath = scanner.nextLine();
        if (filePath.equals("1")) {
            return;
        }

        try {
            options.saveState(filePath);
            System.out.println("Saved state successfully to: " + filePath);
        } catch (Exception e) {
            System.out.println("Error saving state: " + e.getMessage());
        }
    }
}
