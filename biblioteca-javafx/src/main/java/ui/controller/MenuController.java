package ui.controller;

import app.SceneRouter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MenuController {

    @FXML
    public void onAutores(ActionEvent e) { SceneRouter.go("autores.fxml"); }

    @FXML
    public void onLibros(ActionEvent e) { SceneRouter.go("libros.fxml"); }

    @FXML
    public void onSalir(ActionEvent e) { System.exit(0); }
}
