package command.impl;

import api.EngineOptions;
import command.api.Command;

import java.util.Scanner;

public class LoadXmlFile implements Command {
    private final EngineOptions options;

    public LoadXmlFile(EngineOptions options) {
        this.options = options;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter the full path to the XML file, or '1' to return to the main menu:");
            String path = scanner.nextLine();
            if (path.equals("1")) {
                return;
            }
            try {
                options.SetNewSheet(path);
                System.out.println("Successfully loaded XML file!");
                return;
            } catch (Exception e) {
                System.out.println("Error loading XML file!\nError: " + e.getMessage());
                System.out.println("Please try again");
            }
        }
    }
}
