package ui.controller;

import app.SceneRouter;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import model.Autor;
import model.Libro;
import service.BibliotecaService;
import util.Validations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class LibrosController {

    @FXML private TextField txtTitulo;
    @FXML private TextField txtIsbn;
    @FXML private TextField txtAnio;

    // Debe coincidir con libros.fxml: fx:id="cbAutor"
    @FXML private ComboBox<Autor> cbAutor;

    @FXML private CheckBox chkActivo;

    // Debe coincidir con libros.fxml: fx:id="listLibros"
    @FXML private ListView<Libro> listLibros;

    @FXML private TableView<Libro> tblLibros;
    @FXML private TableColumn<Libro, Integer> colId;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colIsbn;
    @FXML private TableColumn<Libro, Integer> colAnio;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, Boolean> colActivo;

    private final BibliotecaService service = new BibliotecaService();
    private final ObservableList<Libro> librosObs = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        // Datos en list y tabla
        listLibros.setItems(librosObs);
        tblLibros.setItems(librosObs);

        // Cargar autores al combo
        cbAutor.setItems(FXCollections.observableArrayList(service.getAutores()));
        cbAutor.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Autor a, boolean empty) {
                super.updateItem(a, empty);
                setText(empty || a == null ? null : a.getNombre());
            }
        });
        cbAutor.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Autor a, boolean empty) {
                super.updateItem(a, empty);
                setText(empty || a == null ? null : a.getNombre());
            }
        });

        // ListView render
        listLibros.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Libro l, boolean empty) {
                super.updateItem(l, empty);
                if (empty || l == null) {
                    setText(null);
                } else {
                    String autorNombre = service.getAutorNombreById(l.getAutorId());
                    setText(l.getId() + " - " + l.getTitulo()
                            + " | ISBN: " + l.getIsbn()
                            + " | Año: " + l.getAnio()
                            + " | Autor: " + autorNombre
                            + " | Activo: " + (l.isActivo() ? "Sí" : "No"));
                }
            }
        });

        // Columnas tabla
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colIsbn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIsbn()));
        colAnio.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getAnio()).asObject());
        colAutor.setCellValueFactory(c -> new SimpleStringProperty(service.getAutorNombreById(c.getValue().getAutorId())));
        colActivo.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isActivo()).asObject());

        // Tabla oculta de inicio (para que el botón tenga sentido)
        tblLibros.setVisible(false);
        tblLibros.setManaged(false);

        // Selección desde list -> rellena form
        listLibros.getSelectionModel().selectedItemProperty().addListener((obs, o, sel) -> {
            if (sel != null) cargarEnFormulario(sel);
        });

        // Selección desde tabla -> rellena form
        tblLibros.getSelectionModel().selectedItemProperty().addListener((obs, o, sel) -> {
            if (sel != null) cargarEnFormulario(sel);
        });

        refrescarDatos();
    }

    @FXML
    public void onNuevo(ActionEvent event) {
        listLibros.getSelectionModel().clearSelection();
        tblLibros.getSelectionModel().clearSelection();
        limpiarFormulario();
        txtTitulo.requestFocus();
    }

    @FXML
    public void onGuardar(ActionEvent event) {
        String titulo = txtTitulo.getText() == null ? "" : txtTitulo.getText().trim();
        String isbn = txtIsbn.getText() == null ? "" : txtIsbn.getText().trim();
        String anioStr = txtAnio.getText() == null ? "" : txtAnio.getText().trim();
        Autor autor = cbAutor.getValue();
        boolean activo = chkActivo.isSelected();

        if (Validations.isBlank(titulo) || Validations.isBlank(isbn) || Validations.isBlank(anioStr) || autor == null) {
            error("Todos los campos son obligatorios.");
            return;
        }

        int anio;
        try {
            anio = Integer.parseInt(anioStr);
        } catch (NumberFormatException e) {
            error("El año debe ser numérico.");
            return;
        }

        Libro l = new Libro(0, titulo, isbn, anio, autor.getId(), activo);
        service.addLibro(l);

        refrescarDatos();
        limpiarFormulario();
        info("Libro guardado (ID: " + l.getId() + ")");
    }

    @FXML
    public void onEditar(ActionEvent event) {
        Libro sel = getSeleccionado();
        if (sel == null) {
            error("Selecciona un libro para modificar.");
            return;
        }

        String titulo = txtTitulo.getText() == null ? "" : txtTitulo.getText().trim();
        String isbn = txtIsbn.getText() == null ? "" : txtIsbn.getText().trim();
        String anioStr = txtAnio.getText() == null ? "" : txtAnio.getText().trim();
        Autor autor = cbAutor.getValue();

        if (Validations.isBlank(titulo) || Validations.isBlank(isbn) || Validations.isBlank(anioStr) || autor == null) {
            error("Todos los campos son obligatorios.");
            return;
        }

        int anio;
        try {
            anio = Integer.parseInt(anioStr);
        } catch (NumberFormatException e) {
            error("El año debe ser numérico.");
            return;
        }

        sel.setTitulo(titulo);
        sel.setIsbn(isbn);
        sel.setAnio(anio);
        sel.setAutorId(autor.getId());
        sel.setActivo(chkActivo.isSelected());

        service.updateLibro(sel);

        refrescarDatos();
        info("Libro modificado.");
    }

    @FXML
    public void onEliminar(ActionEvent event) {
        Libro sel = getSeleccionado();
        if (sel == null) {
            error("Selecciona un libro para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar el libro seleccionado (ID " + sel.getId() + ")?",
                ButtonType.OK, ButtonType.CANCEL);

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                service.deleteLibro(sel);
                refrescarDatos();
                limpiarFormulario();
                info("Libro eliminado.");
            }
        });
    }

    @FXML
    public void onToggleTabla(ActionEvent event) {
        boolean visible = !tblLibros.isVisible();
        tblLibros.setVisible(visible);
        tblLibros.setManaged(visible);
    }

    @FXML
    public void onExportar(ActionEvent event) {
        Path origen = Path.of("data", "libros.json");

        if (!Files.exists(origen)) {
            error("No existe el fichero data/libros.json todavía. Guarda algún libro primero.");
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Exportar libros (JSON)");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON (*.json)", "*.json"));
        fc.setInitialFileName("libros.json");

        File destino = fc.showSaveDialog(((javafx.scene.Node) event.getSource()).getScene().getWindow());
        if (destino == null) return;

        try {
            Files.copy(origen, destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            info("Exportación completada:\n" + destino.getAbsolutePath());
        } catch (IOException e) {
            error("No se pudo exportar el fichero: " + e.getMessage());
        }
    }

    @FXML
    public void onVolver(ActionEvent event) {
        // Si usáis SceneRouter, cambiáis a SceneRouter.go("menu.fxml");
        SceneRouter.go("Menu.fxml");
    }

    private Libro getSeleccionado() {
        Libro sel = listLibros.getSelectionModel().getSelectedItem();
        if (sel == null) sel = tblLibros.getSelectionModel().getSelectedItem();
        return sel;
    }

    private void cargarEnFormulario(Libro l) {
        if (l == null) return;

        txtTitulo.setText(l.getTitulo());
        txtIsbn.setText(l.getIsbn());
        txtAnio.setText(String.valueOf(l.getAnio()));
        chkActivo.setSelected(l.isActivo());

        Autor autor = service.getAutorById(l.getAutorId());
        cbAutor.setValue(autor);
    }

    private void limpiarFormulario() {
        txtTitulo.clear();
        txtIsbn.clear();
        txtAnio.clear();
        cbAutor.setValue(null);
        chkActivo.setSelected(true);
    }

    private void refrescarDatos() {
        librosObs.setAll(service.getLibros());
    }

    private void info(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    private void error(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}


