package command.impl;

import api.EngineOptions;
import command.api.Command;

import java.util.Scanner;

public class UpdateCellValue implements Command {
    private final EngineOptions options;

    public UpdateCellValue(final EngineOptions options) {
        this.options = options;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter '1' to end the editing session and return to the main menu.");
            System.out.println("Or a cell location to edit: ");
            String cellName = scanner.nextLine();
            if (cellName.equals("1")) {
                options.endEditingSession();
                return;
            }
            try {
                System.out.println("Enter the cell's new value: ");
                String cellValue = scanner.nextLine();
                options.setCellValue(cellName, cellValue);
                System.out.println("Update Successful!");
            }catch (Exception e) {
                System.out.println("Update Failed!");
                System.out.println("Error: " + e.getMessage());
            }

        }
    }
}
