package shticell.javafx.Controllers;

import engine.SheetDTO;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.function.Consumer;

public class VersionsWindow {

    private final ArrayList<SheetDTO> versions;
    private final Consumer<SheetDTO> loadVersionCallback;

    public VersionsWindow(ArrayList<SheetDTO> versions, Consumer<SheetDTO> loadVersionCallback) {
        this.versions = versions;
        this.loadVersionCallback = loadVersionCallback;
    }


    public void display(Stage parentStage) {
        Stage stage = new Stage();
        stage.setTitle("Sheet Versions");

        // Set the parent stage as owner and make this window modal
        stage.initOwner(parentStage);
        stage.initModality(Modality.WINDOW_MODAL);

        VBox container = new VBox();
        container.setPadding(new Insets(10));
        container.setSpacing(10);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(container);
        scrollPane.setFitToWidth(true);

        for (SheetDTO version : versions) {
            GridPane gridPane = createVersionGrid(version, stage);
            container.getChildren().add(gridPane);
        }

        Scene scene = new Scene(scrollPane, 800, 600); // Adjusted dimensions for better view
        stage.setScene(scene);
        stage.show();
    }

    private GridPane createVersionGrid(SheetDTO version, Stage stage) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(10));
        gridPane.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

        String[][] cells = version.getEffectiveDataOfCells();
        int originalColumnWidth = version.getColumnWidth();
        int originalRowHeight = version.getRowHeight();

        // Screen dimensions
        double screenWidth = javafx.stage.Screen.getPrimary().getBounds().getWidth();
        double screenHeight = javafx.stage.Screen.getPrimary().getBounds().getHeight();

        // Calculate scale factors to fit the table into a quarter of the screen
        double maxTableWidth = screenWidth / 2; // Half the screen width
        double maxTableHeight = screenHeight / 2; // Half the screen height

        double scaleFactorWidth = maxTableWidth / (originalColumnWidth * cells[0].length);
        double scaleFactorHeight = maxTableHeight / (originalRowHeight * cells.length);
        double scaleFactor = Math.min(scaleFactorWidth, scaleFactorHeight); // Choose the smaller scale factor

        // Adjusted cell dimensions
        int scaledColumnWidth = (int) (originalColumnWidth * scaleFactor);
        int scaledRowHeight = (int) (originalRowHeight * scaleFactor);

        // Add column titles (letters)
        for (int col = 0; col < cells[0].length; col++) {
            String columnTitle = String.valueOf((char) ('A' + col)); // Convert to letters
            Text colLabel = new Text(columnTitle);
            colLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
            gridPane.add(colLabel, col + 1, 0); // Offset by 1 to account for row titles
        }

        // Add row titles (numbers)
        for (int row = 0; row < cells.length; row++) {
            String rowTitle = String.valueOf(row + 1); // Convert to numbers
            Text rowLabel = new Text(rowTitle);
            rowLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
            gridPane.add(rowLabel, 0, row + 1); // Offset by 1 to account for column titles
        }

        // Add cell data
        for (int row = 0; row < cells.length; row++) {
            for (int col = 0; col < cells[row].length; col++) {
                String effectiveValue = cells[row][col] != null ? cells[row][col] : " ";
                String cellName = (char) ('A' + col) + String.valueOf(row + 1);

                // Create and add the TextField
                TextField cellField = createCellField(cellName, effectiveValue, scaledColumnWidth, scaledRowHeight);
                gridPane.add(cellField, col + 1, row + 1);
            }
        }

        gridPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            loadSelectedVersion(version, stage);
        });

        return gridPane;
    }

    private TextField createCellField(String cellName, String effectiveValue, int width, int height) {
        TextField cellField = new TextField(effectiveValue);
        cellField.setPrefSize(width, height);
        cellField.setStyle("-fx-alignment: center;");
        cellField.setEditable(false); // Read-only fields for display
        return cellField;
    }

    private void loadSelectedVersion(SheetDTO version, Stage stage) {
        // Pass the selected version to the callback
        loadVersionCallback.accept(version);
        stage.close(); // Close the versions window
    }
}