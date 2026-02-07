package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Cargar autores.fxml desde resources
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ui/view/libros.fxml")
            );

            Scene scene = new Scene(loader.load(), 900, 600);

            stage.setTitle("Biblioteca");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            // Si falla el FXML, lo veremos claro por consola
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
