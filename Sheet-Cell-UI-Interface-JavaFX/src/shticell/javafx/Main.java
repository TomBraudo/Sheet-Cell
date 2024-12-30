package shticell.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Controllers/fxml/menu.fxml"));
        Scene scene = new Scene(loader.load());

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/resources/styles/grid.css")).toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Main Menu");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
