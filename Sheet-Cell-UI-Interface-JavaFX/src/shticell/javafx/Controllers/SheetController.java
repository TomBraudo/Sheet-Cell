package shticell.javafx.Controllers;

import api.EngineOptions;
import engine.SheetDTO;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class SheetController {

    private EngineOptions engineOptions;

    @FXML
    private GridPane gridPane;

    /**
     * Sets the EngineOptions instance for this controller.
     *
     * @param engineOptions The EngineOptions instance to use.
     */
    public void setEngineOptions(EngineOptions engineOptions) {
        this.engineOptions = engineOptions;
        displaySheet(); // Immediately display the sheet
    }

    private void displaySheet() {

        if (engineOptions == null) {
            System.out.println("EngineOptions is not set!");
            return;
        }

        SheetDTO sheetDTO = engineOptions.getCurSheet();
        if (sheetDTO == null) {
            System.out.println("No sheet available.");
            return;
        }

        // Get the sheet data and dimensions
        String[][] data = sheetDTO.getEffectiveDataOfCells();
        int originalColumnWidth = sheetDTO.getColumnWidth();
        int originalRowHeight = sheetDTO.getRowHeight();

        // Screen dimensions
        double screenWidth = javafx.stage.Screen.getPrimary().getBounds().getWidth();
        double screenHeight = javafx.stage.Screen.getPrimary().getBounds().getHeight();

        // Calculate scale factors to fit the table into a quarter of the screen
        double maxTableWidth = screenWidth / 2; // Half the screen width
        double maxTableHeight = screenHeight / 2; // Half the screen height

        double scaleFactorWidth = maxTableWidth / (originalColumnWidth * data[0].length);
        double scaleFactorHeight = maxTableHeight / (originalRowHeight * data.length);
        double scaleFactor = Math.min(scaleFactorWidth, scaleFactorHeight); // Choose the smaller scale factor

        // Adjusted cell dimensions
        int scaledColumnWidth = (int) (originalColumnWidth * scaleFactor);
        int scaledRowHeight = (int) (originalRowHeight * scaleFactor);

        // Clear previous content in the GridPane
        gridPane.getChildren().clear();

        // Add column titles (letters)
        for (int col = 0; col < data[0].length; col++) {
            String columnTitle = String.valueOf((char) ('A' + col)); // Convert to letters
            Label colLabel = new Label(columnTitle);
            colLabel.setPrefSize(scaledColumnWidth, scaledRowHeight);
            colLabel.setStyle("-fx-alignment: center; -fx-font-weight: bold;");
            gridPane.add(colLabel, col + 1, 0); // Offset by 1 to account for row titles
        }

        // Add row titles (numbers)
        for (int row = 0; row < data.length; row++) {
            String rowTitle = String.valueOf(row + 1); // Convert to numbers
            Label rowLabel = new Label(rowTitle);
            rowLabel.setPrefSize(scaledColumnWidth, scaledRowHeight);
            rowLabel.setStyle("-fx-alignment: center; -fx-font-weight: bold;");
            gridPane.add(rowLabel, 0, row + 1); // Offset by 1 to account for column titles
        }

        // Add cell data
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[row].length; col++) {
                String value = data[row][col];

                // Create a Label for each cell
                Label cellLabel = new Label(value == null || value.isEmpty() ? "" : value);
                cellLabel.setPrefSize(scaledColumnWidth, scaledRowHeight); // Set scaled cell dimensions
                cellLabel.getStyleClass().add("cell");
                cellLabel.setPadding(new Insets(5)); // Add padding inside the cell

                // Add the cell to the GridPane
                gridPane.add(cellLabel, col + 1, row + 1); // Offset by 1 to account for titles
            }
        }

        // Remove debug grid lines
        gridPane.setGridLinesVisible(false);
    }
}
