package app;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        SceneRouter.setStage(stage);
        SceneRouter.go("menu.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

