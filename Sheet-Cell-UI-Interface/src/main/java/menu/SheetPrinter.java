package menu;

import engine.SheetDTO;

public class SheetPrinter {
    public static void printSheet(SheetDTO sheet) {
        int rows = sheet.getEffectiveDataOfCells()[0].length; // Get the number of rows
        int columns = sheet.getEffectiveDataOfCells().length; // Get the number of columns
        int columnWidth = sheet.getColumnWidth(); // Get the column width
        int rowHeight = sheet.getRowHeight();
        // Create a dynamic format string for the cells
        String cellFormat = "%-" + columnWidth + "s";

        // Print the table header (columns as letters)
        System.out.printf(cellFormat + " ", "Row\\Col");
        for (int col = 0; col < columns; col++) {
            System.out.printf(cellFormat + " ", (char) ('A' + col)); // Print column letters (A, B, C...)
        }
        System.out.println();
        System.out.println("-".repeat((columns) * (columnWidth + 3) - 3)); // Separator line

        String[][] values = sheet.getEffectiveDataOfCells();
        // Print each row
        for (int row = 1; row <= rows; row++) {
            // Print the row number
            System.out.printf(cellFormat + "|", row);

            // Print the values for each column
            for (int col = 0; col < columns; col++) {

                String cellValue = values[row-1][col];
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
