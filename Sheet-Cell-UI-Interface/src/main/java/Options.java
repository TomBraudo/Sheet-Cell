import com.options.api.*;

import java.util.Formatter;

public class Options {
    private final Sheet sheet;

    public Options(String filePath) {
        sheet = new Sheet(filePath); // Load the sheet from the XML file
    }

    public void showTable() {
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
}
