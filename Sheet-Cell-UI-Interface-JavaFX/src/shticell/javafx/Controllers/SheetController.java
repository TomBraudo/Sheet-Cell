package shticell.javafx.Controllers;

import api.EngineOptions;
import engine.CellDTO;
import engine.SheetDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SheetController {

    private EngineOptions engineOptions;

    private boolean isSelectingRange = false;
    private TextField activeCellField; // Tracks the currently active cell for editing or range selection
    private String startCell; // Tracks the starting cell for the range
    private String endCell; // Tracks the ending cell for the range

    @FXML
    private GridPane gridPane;
    @FXML
    private Button SaveStateBtn;

    private final Map<String, Node> cellMap = new HashMap<>(); // Map to store all cells by their name

    public void setEngineOptions(EngineOptions engineOptions) {
        this.engineOptions = engineOptions;
        displaySheet(); // Immediately display the sheet
    }

    private void displaySheet() {

        if (engineOptions == null) {
            System.out.println("EngineOptions is not set!");
            return;
        }

        SheetDTO sheetDTO = engineOptions.getVersion(0);
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

        // Clear previous content in the GridPane and Map
        gridPane.getChildren().clear();
        cellMap.clear();

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

                cellMap.put(cellName, cellField);
            }
        }



        // Remove debug grid lines
        gridPane.setGridLinesVisible(false);
    }

    private TextField createCellField(String cellName, String effectiveValue, int scaledColumnWidth, int scaledRowHeight) {
        TextField cellField = new TextField(effectiveValue == null ? "" : effectiveValue);
        cellField.setPrefSize(scaledColumnWidth, scaledRowHeight);

        // Handle mouse press to start range selection
        cellField.setOnMousePressed(event -> {
            if (isSelectingRange) {
                startCell = cellName; // Set the starting cell
                endCell = cellName; // Initialize endCell to startCell
                highlightRange(startCell, endCell); // Highlight the initial single cell
            } else {
                if (activeCellField != cellField) {
                    // Enter single-cell editing mode only if the pressed cell is different from the currently active cell
                    enterEditMode(cellField, cellName);
                }
            }
        });

        // Handle mouse drag to dynamically update the range
        cellField.setOnMouseDragged(event -> {
            if (isSelectingRange) {
                endCell = calculateCellFromMousePosition(event.getSceneX(), event.getSceneY());
                if (endCell != null) {
                    highlightRange(startCell, endCell); // Highlight the range dynamically
                }
            }
        });

        // Handle mouse release to finalize the range
        cellField.setOnMouseReleased(event -> {
            if (isSelectingRange) {
                finalizeRangeSelection(); // Finalize the range selection
            }
        });

        // Handle key press for range selection activation
        cellField.setOnKeyPressed(event -> {
            if (event.isControlDown()) {
                isSelectingRange = true; // Enable range selection when Control is held
                clearRangeHighlight(); // Prepare for a new range
            }
        });

        // Handle key release for range selection deactivation
        cellField.setOnKeyReleased(event -> {
            if (!event.isControlDown() && isSelectingRange) {
                isSelectingRange = false; // Disable range selection when Control is released
            }
        });

        // Handle pressing Enter to finalize editing
        cellField.setOnAction(event -> {
            finalizeCellEditing(cellField, cellName);
        });

        // Handle focus loss to finalize editing
        cellField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !isSelectingRange) { // Focus lost
                if (activeCellField == cellField) {
                    finalizeCellEditing(cellField, cellName);
                }
            }
        });

        return cellField;
    }

    private String calculateCellFromMousePosition(double mouseX, double mouseY) {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof TextField) {
                Bounds bounds = node.localToScene(node.getBoundsInLocal());
                if (bounds.contains(mouseX, mouseY)) {
                    int row = GridPane.getRowIndex(node) - 1; // Adjust to zero-based index
                    int col = GridPane.getColumnIndex(node) - 1; // Adjust to zero-based index
                    return (char) ('A' + col) + String.valueOf(row + 1); // Convert to cell name
                }
            }
        }
        return null;
    }

    private String getCorrectRange(){
        // Parse the rows and columns
        char startColumn = startCell.charAt(0); // First character for the column
        char endColumn = endCell.charAt(0);

        int startRow = Integer.parseInt(startCell.substring(1)); // Remaining characters for the row
        int endRow = Integer.parseInt(endCell.substring(1));

        // Calculate the top-left and bottom-right corners
        char minColumn = (char) Math.min(startColumn, endColumn); // Smaller column letter
        char maxColumn = (char) Math.max(startColumn, endColumn); // Larger column letter

        int minRow = Math.min(startRow, endRow); // Smaller row number
        int maxRow = Math.max(startRow, endRow); // Larger row number

        // Construct the range
        String minCell = minColumn + String.valueOf(minRow);
        String maxCell = maxColumn + String.valueOf(maxRow);
        return minCell + ":" + maxCell;
    }

    private void highlightDependencies(String cellName) {
        clearRangeHighlight(); // Clear any existing highlights

        // Highlight cells this cell depends on (in blue)
        List<String> dependencies = engineOptions.getDependentOn(cellName);
        for (String dependency : dependencies) {
            Node node = cellMap.get(dependency);
            if (node != null) {
                node.setStyle("-fx-background-color: lightblue; -fx-border-color: darkblue; -fx-border-width: 1px;");
            }
        }

        // Highlight cells that depend on this cell (in green)
        List<String> dependents = engineOptions.getDependents(cellName);
        for (String dependent : dependents) {
            Node node = cellMap.get(dependent);
            if (node != null) {
                node.setStyle("-fx-background-color: lightgreen; -fx-border-color: darkgreen; -fx-border-width: 1px;");
            }
        }
    }

    private void finalizeRangeSelection() {
        // Determine the range based on startCell and endCell
        String range = getCorrectRange();

        // Insert the selected range into the active cell's text
        if (activeCellField != null) {
            activeCellField.setText(activeCellField.getText() + range);
            activeCellField.requestFocus(); // Refocus on the active cell
            activeCellField.selectEnd(); // Move the caret to the end of the text
        }

        // Clear the range and reset highlighting
        startCell = null;
        endCell = null;
        clearRangeHighlight();
    }

    private void finalizeCellEditing(TextField cellField, String cellName) {
        if (activeCellField == cellField && cellField.isEditable()) {
            String newRawValue = cellField.getText(); // Get the edited value
            engineOptions.setCellValue(cellName, newRawValue); // Update the cell in the engine
            String newEffectiveValue = engineOptions.getCellData(cellName).getEffectiveValue(); // Get the recalculated value
            cellField.setText(newEffectiveValue == null ? "" : newEffectiveValue); // Display the effective value
            updateDependentCells(cellName); // Update dependent cells
            cellField.setEditable(false); // Exit editing mode
            clearRangeHighlight();
        }
    }

    private void enterEditMode(TextField cellField, String cellName) {
        if (activeCellField != cellField || !cellField.isEditable()) {
            activeCellField = cellField; // Track the active cell
            highlightDependencies(cellName);
            String actualValue = engineOptions.getCellData(cellName).getOriginalValue();
            cellField.setText(actualValue == null ? "" : actualValue); // Show the raw value
            cellField.setEditable(true); // Enable editing mode
            cellField.requestFocus(); // Focus on the cell
            cellField.selectAll(); // Select all text for editing
        }
    }

    private void highlightRange(String startCell, String endCell) {
        int startRow = engineOptions.getCellData(startCell).getRowFromCellName();
        int startCol = engineOptions.getCellData(startCell).getColFromCellName();
        int endRow = engineOptions.getCellData(endCell).getRowFromCellName();
        int endCol = engineOptions.getCellData(endCell).getColFromCellName();

        for (Map.Entry<String, Node> entry : cellMap.entrySet()) {
            String cellName = entry.getKey();
            Node node = entry.getValue();

            int cellRow = engineOptions.getCellData(cellName).getRowFromCellName();
            int cellCol = engineOptions.getCellData(cellName).getColFromCellName();

            if (cellRow >= Math.min(startRow, endRow) && cellRow <= Math.max(startRow, endRow)
                    && cellCol >= Math.min(startCol, endCol) && cellCol <= Math.max(startCol, endCol)) {
                node.setStyle("-fx-background-color: lightblue; -fx-border-color: darkblue; -fx-border-width: 1px;");
            } else {
                node.setStyle("");
            }
        }
    }


    private void clearRangeHighlight() {
        for (Node node : gridPane.getChildren()) {
            node.setStyle(""); // Reset style for all cells
        }
    }

    @FXML
    private void initialize() {
        // Initialization logic, if necessary
    }


    private void updateDependentCells(String cellName) {
        List<String> dependents = engineOptions.getDependents(cellName); // Get dependents from the engine

        for (String dependentCellName : dependents) {
            int row = engineOptions.getCellData(dependentCellName).getRowFromCellName();
            int col = engineOptions.getCellData(dependentCellName).getColFromCellName();

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

    public void saveVersion(ActionEvent actionEvent) {
        engineOptions.endEditingSession();
    }

    public void openVersionsWindow(ActionEvent actionEvent) {
        if (engineOptions == null) {
            System.out.println("EngineOptions is not set!");
            return;
        }

        // Retrieve all versions from the engine
        List<SheetDTO> versions = engineOptions.getVersionsData();
        if (versions == null || versions.isEmpty()) {
            System.out.println("No versions available.");
            return;
        }

        Stage currentStage = (Stage) gridPane.getScene().getWindow();

        // Create and display the VersionsWindow
        VersionsWindow versionsWindow = new VersionsWindow(new ArrayList<>(versions), selectedVersion -> {
            // Callback to handle loading the selected version
            engineOptions.loadVersion(selectedVersion.getVersion());
            refreshAllCells();
        });

        versionsWindow.display(currentStage);
    }

    private void refreshAllCells() {
        for (Map.Entry<String, Node> entry : cellMap.entrySet()) {
            String cellName = entry.getKey();
            TextField cellField = (TextField) entry.getValue();
            cellField.setText(engineOptions.getCellData(cellName).getEffectiveValue());
        }
    }



}
