package ui.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Libro;
import model.Autor;
import service.BibliotecaService;

public class LibrosController {

    @FXML private TextField txtTitulo;
    @FXML private TextField txtIsbn;
    @FXML private TextField txtAnio;
    @FXML private ComboBox<Autor> cbAutor;
    @FXML private CheckBox chkActivo;

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

        // Combo autores
        cbAutor.setItems(FXCollections.observableArrayList(service.getAutores()));

        // ListView
        listLibros.setItems(librosObs);
        listLibros.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Libro libro, boolean empty) {
                super.updateItem(libro, empty);
                if (empty || libro == null) {
                    setText(null);
                } else {
                    setText(libro.getTitulo() + " | ISBN: " + libro.getIsbn() +
                            " | Año: " + libro.getAnio() +
                            " | Autor: " + service.getNombreAutor(libro.getAutorId()) +
                            " | Activo: " + (libro.isActivo() ? "Sí" : "No"));
                }
            }
        });

        // Tabla
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colIsbn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIsbn()));
        colAnio.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getAnio()).asObject());
        colAutor.setCellValueFactory(c -> new SimpleStringProperty(service.getNombreAutor(c.getValue().getAutorId())));
        colActivo.setCellValueFactory(c -> new javafx.beans.property.SimpleBooleanProperty(c.getValue().isActivo()));

        tblLibros.setItems(librosObs);
        tblLibros.setVisible(false);

        // Selección
        listLibros.getSelectionModel().selectedItemProperty().addListener((obs, o, sel) -> cargarEnFormulario(sel));

        refrescarDatos();
    }

    @FXML
    private void onNuevo() {
        listLibros.getSelectionModel().clearSelection();
        limpiarFormulario();
    }

    @FXML
    private void onGuardar() {
        try {
            Libro l = new Libro(
                    0,
                    txtTitulo.getText().trim(),
                    txtIsbn.getText().trim(),
                    Integer.parseInt(txtAnio.getText()),
                    cbAutor.getValue().getId(),
                    chkActivo.isSelected()
            );
            service.addLibro(l);
            refrescarDatos();
            limpiarFormulario();
            info("Libro guardado.");
        } catch (Exception e) {
            error("Datos incorrectos.");
        }
    }

    @FXML
    private void onModificar() {
        Libro sel = listLibros.getSelectionModel().getSelectedItem();
        if (sel == null) {
            error("Selecciona un libro para modificar.");
            return;
        }

        sel.setTitulo(txtTitulo.getText().trim());
        sel.setIsbn(txtIsbn.getText().trim());
        sel.setAnio(Integer.parseInt(txtAnio.getText()));
        sel.setAutorId(cbAutor.getValue().getId());
        sel.setActivo(chkActivo.isSelected());

        service.updateLibro(sel);
        refrescarDatos();
        info("Libro modificado.");
    }

    @FXML
    private void onEliminar() {
        Libro sel = listLibros.getSelectionModel().getSelectedItem();
        if (sel == null) {
            error("Selecciona un libro para eliminar.");
            return;
        }

        service.deleteLibro(sel);
        refrescarDatos();
        limpiarFormulario();
        info("Libro eliminado.");
    }

    @FXML
    private void onToggleTabla() {
        tblLibros.setVisible(!tblLibros.isVisible());
    }

    @FXML
    private void onVolver() {
        Platform.exit();
    }

    private void cargarEnFormulario(Libro l) {
        if (l == null) return;

        txtTitulo.setText(l.getTitulo());
        txtIsbn.setText(l.getIsbn());
        txtAnio.setText(String.valueOf(l.getAnio()));
        chkActivo.setSelected(l.isActivo());

        cbAutor.getItems().stream()
                .filter(a -> a.getId() == l.getAutorId())
                .findFirst()
                .ifPresent(cbAutor::setValue);
    }

    private void limpiarFormulario() {
        txtTitulo.clear();
        txtIsbn.clear();
        txtAnio.clear();
        cbAutor.getSelectionModel().clearSelection();
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
