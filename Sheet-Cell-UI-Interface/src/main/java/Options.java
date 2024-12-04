import com.options.api.*;

import java.lang.StringBuilder;
import java.util.HashMap;
import java.util.Map;

public class Options {

    private Sheet sheet;
    private EngineOptions engineOptions;
    private Map<UserChoices, Command> userChoicesActions;

    public Options(String filePath) {
        this.engineOptions = new EngineOptions(filePath);
        this.sheet = engineOptions.getCurSheet();
        initializeUserChoicesActions();

    }

    public void executeUserChoice(UserChoices userChoice, String... args) {
        Command action = userChoicesActions.get(userChoice);
        if (action != null) {
            action.execute(args);
        }
        else {
            throw new IllegalArgumentException("Invalid user choice " + userChoice);
        }
    }

    private void initializeUserChoicesActions() {
        userChoicesActions = new HashMap<UserChoices, Command>();
        userChoicesActions.put(UserChoices.LOAD_NEW_SHEET, this::loadNewSheet);
        userChoicesActions.put(UserChoices.DISPLAY_SHEET, this::showSheet);
        userChoicesActions.put(UserChoices.CHANGE_CELL_VALUE, this::changeCellValue);
        userChoicesActions.put(UserChoices.DISPLAY_SINGLE_CELL, this::showCellValue);
    }

    public void loadNewSheet(String... args) {
        if(args.length != 1) {
            throw new IllegalArgumentException("Invalid number of arguments");
        }
        this.engineOptions = new EngineOptions(args[0]);
        this.sheet = engineOptions.getCurSheet();
    }

    public void showSheet(String... args) {
        if(args.length != 0) {
            throw new IllegalArgumentException("Invalid number of arguments");
        }
        int rows = sheet.getRows(); // Get the number of rows
        int columns = sheet.getColumns(); // Get the number of columns
        int columnWidth = sheet.getColumnWidth(); // Get the column width
        int rowHeight = sheet.getRowsHeight(); // Get the row height

        // Create a dynamic format string for the cells
        String cellFormat = "%-" + columnWidth + "s";

        // Print the table header (columns as letters)
        System.out.printf(cellFormat + " ", "Row\\Col");
        for (int col = 0; col < columns; col++) {
            System.out.printf(cellFormat + " ", (char) ('A' + col)); // Print column letters (A, B, C...)
        }
        System.out.println();
        System.out.println("-".repeat((columns) * (columnWidth + 3) - 3)); // Separator line

        // Print each row
        for (int row = 1; row <= rows; row++) {
            // Print the row number
            System.out.printf(cellFormat + "|", row);

            // Print the values for each column
            for (int col = 0; col < columns; col++) {
                // Get the cell value using the "letter-row" format
                String cellName = (char) ('A' + col) + Integer.toString(row);
                String cellValue = sheet.getCellValue(cellName);

                // Replace null or empty values with a space
                cellValue = (cellValue == null || cellValue.isEmpty()) ? " " : cellValue;
                System.out.printf(cellFormat + "|", cellValue);
            }
            System.out.println();

            // Add extra spacing for row height
            for (int h = 1; h < rowHeight; h++) {
                System.out.printf(cellFormat + "|", " ");
                for (int col = 0; col < columns; col++) {
                    System.out.printf(cellFormat + "|", " ");
                }
                System.out.println();
            }
        }
    }

    public void changeCellValue(String... args) {
        if(args.length != 2) {
            throw new IllegalArgumentException("Invalid number of arguments");
        }
        sheet.setCell(args[0], args[1]);
    }

    public void showCellValue(String... args) {
        if(args.length != 1) {
            throw new IllegalArgumentException("Invalid number of arguments");
        }
        CellData cellData = engineOptions.getCellData(args[0]);
        StringBuilder sb = new StringBuilder();
        sb.append("Cell " + args[0] + ": " + "\n");
        sb.append("Original Value: " + cellData.getOriginalValue() + "\n");
        sb.append("Effective Value: " + cellData.getEffectiveValue() + "\n");
        sb.append("Dependent On: " + "\n");
        for (String dependent : cellData.getDependentOn()) {
            sb.append(dependent + ", ");
        }
        sb.append("\b\b\n");
        sb.append("Dependents: " + "\n");
        for (String dependent : cellData.getDependents()) {
            sb.append(dependent + ", ");
        }
        sb.append("\b\b\n");

        System.out.println(sb.toString());
    }


}
