package shticell.javafx.Controllers;

import api.EngineOptions;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import shticell.javafx.Controllers.SheetController;

import java.io.File;

public class MenuController {

    private final EngineOptions engineOptions = new EngineOptions();

    @FXML
    public void loadSheetFromXML() {
        try {
            // Prompt for XML file path
            String filePath = getPathFromFileChooser(new ExtensionFilter("XML files", "*.xml"));
            engineOptions.SetNewSheet(filePath);
            openSheetWindow(engineOptions);
        } catch (Exception e) {
            System.err.println("Failed to load sheet from XML: " + e.getMessage());
        }
    }

    @FXML
    public void loadExistingState() {
        try {
            // Prompt for state file path
            String filePath = getPathFromFileChooser(new ExtensionFilter("State files", "*.state"));
            engineOptions.loadState(filePath);
            openSheetWindow(engineOptions);
        } catch (Exception e) {
            System.err.println("Failed to load existing state: " + e.getMessage());
        }
    }

    private String getPathFromFileChooser(ExtensionFilter extensionFilter) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().add(extensionFilter);

        // Set initial directory (user's home directory as fallback)
        File initialDir = new File("C:/");
        if (!initialDir.exists() || !initialDir.isDirectory()) {
            initialDir = new File(System.getProperty("user.home"));
        }
        fileChooser.setInitialDirectory(initialDir);

        // Open file chooser
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        } else {
            throw new IllegalArgumentException("File not selected");
        }
    }

    private void openSheetWindow(EngineOptions engineOptions) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/sheet.fxml"));

            // Load the FXML and create the stage
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);

            // Pass the EngineOptions to the SheetController
            SheetController controller = loader.getController();
            controller.setEngineOptions(engineOptions);

            // Apply CSS
            scene.getStylesheets().add(getClass().getResource("/resources/styles/grid.css").toExternalForm());

            // Set up the stage
            stage.setScene(scene);
            stage.setTitle("Sheet Viewer");
            stage.setResizable(false); // Don't allow resizing
            stage.show();
        } catch (Exception e) {
            System.err.println("Failed to open sheet window: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
