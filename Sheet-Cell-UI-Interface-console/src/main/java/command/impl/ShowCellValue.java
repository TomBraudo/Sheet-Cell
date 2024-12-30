package command.impl;

import api.EngineOptions;
import command.api.Command;
import engine.CellDTO;

import java.util.Scanner;

public class ShowCellValue implements Command {

    private final EngineOptions options;
    public ShowCellValue(final EngineOptions options) {
        this.options = options;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter cell location, or '1' to return to the main menu: ");
            System.out.println("Input must be a column letter A-Z followed by a row number (for example: A1, C15...).");
            String cellName = scanner.nextLine();
            if(cellName.equals("1")) {
                return;
            }
            try{
                CellDTO cell = options.getCellData(cellName);
                displayCellDetails(cell);
                return;
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please try again.");
            }

        }
    }

    private void displayCellDetails(CellDTO cell) {
        StringBuilder sb = new StringBuilder();
        sb.append("Cell " + cell.getLocation() + ": " + "\n");
        sb.append("Original Value: " + cell.getOriginalValue() + "\n");
        sb.append("Effective Value: " + cell.getEffectiveValue() + "\n");
        sb.append("Dependent On: " + "\n");
        for (String dependent : cell.getDependentOn()) {
            sb.append(dependent + ", ");
        }
        sb.append("\b\b\n");
        sb.append("Dependents: " + "\n");
        for (String dependent : cell.getDependents()) {
            sb.append(dependent + ", ");
        }
        sb.append("\b\b\n");

        System.out.println(sb.toString());
    }
}
