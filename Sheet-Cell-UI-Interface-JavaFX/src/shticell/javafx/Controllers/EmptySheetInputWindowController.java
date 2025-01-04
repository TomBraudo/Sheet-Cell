package shticell.javafx.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class EmptySheetInputWindowController {

    public TextField rowsTxt;
    public TextField columnsTxt;
    public TextField rowHeightTxt;
    public TextField columnWidthTxt;

    private Consumer<int[]> callback;

    public void setCallback(Consumer<int[]> callback) {
        this.callback = callback;
    }

    @FXML
    public void createSheet(ActionEvent actionEvent) {
        try {
            int rows = Integer.parseInt(rowsTxt.getText());
            int columns = Integer.parseInt(columnsTxt.getText());
            int rowHeight = Integer.parseInt(rowHeightTxt.getText());
            int columnWidth = Integer.parseInt(columnWidthTxt.getText());

            // Pass the values to the callback
            if (callback != null) {
                callback.accept(new int[]{rows, columns, rowHeight, columnWidth});
            }

            // Close the window
            Stage stage = (Stage) rowsTxt.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            showError("Input Error", "Please ensure all fields contain valid numbers.");
        } catch (IllegalArgumentException e) {
            showError("Error", e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
