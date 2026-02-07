package app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneRouter {
    private static Stage stage;

    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void go(String fxmlPath) {
        try {
            // la primera ventana que veremos será la de menú
            Parent root = FXMLLoader.load(SceneRouter.class.getResource("/ui/view/" + fxmlPath));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar: " + fxmlPath, e);
        }
    }
}
