package shticell.javafx.Controllers;

import api.EngineOptions;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;

public class MenuController {

    private final EngineOptions engineOptions = new EngineOptions();

    @FXML
    private VBox root; // Root layout for the menu
    @FXML
    private ProgressIndicator progressIndicator; // Progress indicator for long-running tasks

    @FXML
    public void loadSheetFromXML() {
        runTaskWithProgress(new ExtensionFilter("XML files", "*.xml"), engineOptions::setNewSheet);
    }

    @FXML
    public void loadExistingState() {
        runTaskWithProgress(new ExtensionFilter("State files", "*.ser", "*.state"), engineOptions::loadState);
    }

    private void runTaskWithProgress(ExtensionFilter extensionFilter, TaskAction taskAction) {
        try {
            String filePath = getPathFromFileChooser(extensionFilter);

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    // Simulate progress over time
                    for (int i = 1; i <= 10; i++) {
                        updateProgress(i, 10); // Update progress
                        Thread.sleep(250); // Simulate work
                    }

                    // Perform the specified action
                    taskAction.execute(filePath);
                    return null;
                }

                @Override
                protected void succeeded() {
                    progressIndicator.setVisible(false); // Hide progress indicator
                    openSheetWindow(engineOptions);
                }

                @Override
                protected void failed() {
                    progressIndicator.setVisible(false); // Hide progress indicator
                    // Show error alert with exception message
                    javafx.application.Platform.runLater(() -> {
                        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                        alert.setTitle("Task Failed");
                        alert.setHeaderText("An error occurred while processing the task.");
                        alert.setContentText(getException().getMessage());

                        alert.showAndWait();
                    });
                }
            };

            bindProgressToTask(task);
            new Thread(task).start();
        } catch (Exception e) {
            System.err.println("Failed to process file: " + e.getMessage());

        }
    }

    public static String getPathFromFileChooser(ExtensionFilter extensionFilter) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().add(extensionFilter);

        // Set initial directory (user's home directory as fallback)
        File initialDir = new File("C:/");
        if (!initialDir.exists() || !initialDir.isDirectory()) {
            initialDir = new File(System.getProperty("user.home"));
        }
        fileChooser.setInitialDirectory(initialDir);

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

    private void bindProgressToTask(Task<?> task) {
        progressIndicator.progressProperty().bind(task.progressProperty());
        progressIndicator.setVisible(true);
    }

    @FXML
    public void openEmptySheetWindow(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/emptySheetInputWindow.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Create New Sheet");

            // Set the callback for receiving sheet dimensions and properties
            EmptySheetInputWindowController controller = loader.getController();
            controller.setCallback(values -> {
                try {
                    // Create a new sheet using the provided dimensions
                    engineOptions.setNewSheet(values[0], values[1], values[2], values[3]);

                    // Open the sheet window and display the blank sheet
                    openSheetWindow(engineOptions);
                } catch (Exception e) {
                    showError("Error", "Failed to create a new sheet: " + e.getMessage());
                }
            });

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to open the sheet creation window.");
        }
    }


    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FunctionalInterface
    private interface TaskAction {
        void execute(String filePath) throws Exception;
    }
}
