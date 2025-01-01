package shticell.javafx.Controllers;

import api.EngineOptions;
import engine.CellDTO;
import engine.SheetDTO;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.awt.event.ActionEvent;
import java.util.List;

public class SheetController {


    private EngineOptions engineOptions;
    private boolean isSelectingRange = false;
    private TextField activeCellField; // The currently edited cell field
    private StringBuilder selectedRange = new StringBuilder(); // Holds the range string


    @FXML
    private GridPane gridPane;
    @FXML
    private Button SaveStateBtn;

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
                String effectiveValue = data[row][col];
                String cellName = (char) ('A' + col) + String.valueOf(row + 1);

                // Create and add the TextField
                TextField cellField = createCellField(cellName, effectiveValue, scaledColumnWidth, scaledRowHeight);
                gridPane.add(cellField, col + 1, row + 1);
            }
        }

// Remove debug grid lines
        gridPane.setGridLinesVisible(false);


        // Remove debug grid lines
        gridPane.setGridLinesVisible(false);
    }
    private TextField createCellField(String cellName, String effectiveValue, int scaledColumnWidth, int scaledRowHeight) {
        // Create a TextField for the cell
        TextField cellField = new TextField(effectiveValue == null ? "" : effectiveValue);
        cellField.setPrefSize(scaledColumnWidth, scaledRowHeight);

        // Flag to track if the cell is in edit mode
        final boolean[] isEditing = {false};

        // Show the raw value and start editing when the user clicks on the cell
        cellField.setOnMouseClicked(event -> {
            if (!isEditing[0]) {
                isEditing[0] = true;
                String actualValue = engineOptions.getCellData(cellName).getOriginalValue();
                cellField.setText(actualValue == null ? "" : actualValue);
                cellField.requestFocus(); // Bring focus to the cell
                cellField.selectAll(); // Select all text for easy editing
            }
        });

        // Handle the user pressing Enter to finalize editing
        cellField.setOnAction(event -> {
            if (isEditing[0]) {
                isEditing[0] = false;
                finalizeEditing(cellField, cellName);
            }
        });

        // Handle focus loss to finalize editing
        cellField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && isEditing[0]) { // Focus lost and editing was in progress
                isEditing[0] = false;
                finalizeEditing(cellField, cellName);
            }
        });

        return cellField;
    }

    private void finalizeEditing(TextField cellField, String cellName) {
        String newRawValue = cellField.getText(); // Get the raw value entered by the user
        engineOptions.setCellValue(cellName, newRawValue); // Update the raw value
        String newEffectiveValue = engineOptions.getCellData(cellName).getEffectiveValue(); // Recalculate the effective value
        cellField.setText(newEffectiveValue == null ? "" : newEffectiveValue); // Show the updated effective value
        updateDependentCells(cellName); // Update dependent cells
        gridPane.requestFocus(); // Remove focus from the cell to simulate Excel-like behavior
    }



    private void updateDependentCells(String cellName) {
        List<String> dependents = engineOptions.getDependents(cellName); // Get dependents from the engine

        for (String dependentCellName : dependents) {
            CellDTO dependentCell = engineOptions.getCellData(dependentCellName);
            int row = dependentCell.getRowFromCellName();
            int col = dependentCell.getColFromCellName();

            // Find the TextField in the GridPane
            for (Node node : gridPane.getChildren()) {
                if (GridPane.getRowIndex(node) == row + 1 && GridPane.getColumnIndex(node) == col + 1) {
                    if (node instanceof TextField) {
                        TextField dependentField = (TextField) node;

                        // Update the effective value in the TextField
                        String effectiveValue = engineOptions.getCellData(dependentCellName).getEffectiveValue();
                        dependentField.setText(effectiveValue == null ? "" : effectiveValue);
                    }
                    break;
                }
            }
        }
    }


    @FXML
    public void saveStateBtnOnAction(javafx.event.ActionEvent actionEvent) {
        String path = MenuController.getPathFromFileChooser(new FileChooser.ExtensionFilter("State Files","*.state","*.ser"));
        engineOptions.saveState(path);
    }
}
