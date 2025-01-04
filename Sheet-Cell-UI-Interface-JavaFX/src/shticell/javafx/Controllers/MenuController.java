package shticell.javafx.Controllers;

import api.EngineOptions;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        runTaskWithProgress(new ExtensionFilter("XML files", "*.xml"), filePath -> {
            engineOptions.SetNewSheet(filePath);
        });
    }

    @FXML
    public void loadExistingState() {
        runTaskWithProgress(new ExtensionFilter("State files", "*.ser", "*.state"), filePath -> {
            engineOptions.loadState(filePath);
        });
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
                    System.err.println("Task failed: " + getException().getMessage());
                    getException().printStackTrace();
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

    @FunctionalInterface
    private interface TaskAction {
        void execute(String filePath) throws Exception;
    }
}
