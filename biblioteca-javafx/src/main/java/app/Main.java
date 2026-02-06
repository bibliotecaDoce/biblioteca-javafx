package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Biblioteca");
        stage.setScene(new Scene(new Label("Biblioteca App"), 400, 300));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
