package ui.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Autor;
import service.BibliotecaService;
import util.Validations;

import java.time.LocalDate;

public class AutoresController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtNacionalidad;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private CheckBox chkActivo;

    @FXML private ListView<Autor> listAutores;
    @FXML private TableView<Autor> tblAutores;
    @FXML private TableColumn<Autor, Integer> colId;
    @FXML private TableColumn<Autor, String> colNombre;
    @FXML private TableColumn<Autor, String> colNacionalidad;
    @FXML private TableColumn<Autor, String> colFechaNacimiento;
    @FXML private TableColumn<Autor, Boolean> colActivo;

    private final BibliotecaService service = new BibliotecaService();
    private final ObservableList<Autor> autoresObs = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        // ListView
        listAutores.setItems(autoresObs);
        listAutores.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Autor a, boolean empty) {
                super.updateItem(a, empty);
                if (empty || a == null) {
                    setText(null);
                } else {
                    setText(a.getNombre() + " | " + a.getNacionalidad() +
                            " | " + a.getFechaNacimiento() +
                            " | Activo: " + (a.isActivo() ? "Sí" : "No"));
                }
            }
        });

        // Tabla
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colNacionalidad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNacionalidad()));
        colFechaNacimiento.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaNacimiento().toString()));
        colActivo.setCellValueFactory(c -> new javafx.beans.property.SimpleBooleanProperty(c.getValue().isActivo()));

        tblAutores.setItems(autoresObs);

        // Selección
        listAutores.getSelectionModel().selectedItemProperty().addListener((obs, o, sel) -> cargarEnFormulario(sel));

        refrescarDatos();
    }

    @FXML
    private void onNuevo() {
        listAutores.getSelectionModel().clearSelection();
        limpiarFormulario();
    }

    @FXML
    private void onGuardar() {
        String nombre = txtNombre.getText().trim();
        String nacionalidad = txtNacionalidad.getText().trim();
        LocalDate fecha = dpFechaNacimiento.getValue();
        boolean activo = chkActivo.isSelected();

        if (Validations.isBlank(nombre) || Validations.isBlank(nacionalidad) || fecha == null) {
            error("Todos los campos son obligatorios.");
            return;
        }

        Autor a = new Autor(0, nombre, nacionalidad, fecha, activo);
        service.addAutor(a);
        refrescarDatos();
        limpiarFormulario();
        info("Autor guardado (ID: " + a.getId() + ")");
    }

    @FXML
    private void onModificar() {
        Autor sel = listAutores.getSelectionModel().getSelectedItem();
        if (sel == null) {
            error("Selecciona un autor para modificar.");
            return;
        }

        sel.setNombre(txtNombre.getText().trim());
        sel.setNacionalidad(txtNacionalidad.getText().trim());
        sel.setFechaNacimiento(dpFechaNacimiento.getValue());
        sel.setActivo(chkActivo.isSelected());

        service.updateAutor(sel);
        refrescarDatos();
        info("Autor modificado.");
    }

    @FXML
    private void onEliminar() {
        Autor sel = listAutores.getSelectionModel().getSelectedItem();
        if (sel == null) {
            error("Selecciona un autor para eliminar.");
            return;
        }

        service.deleteAutor(sel);
        refrescarDatos();
        limpiarFormulario();
        info("Autor eliminado.");
    }

    @FXML
    private void onToggleTabla() {
        tblAutores.setVisible(!tblAutores.isVisible());
    }

    @FXML
    private void onVolver() {
        Platform.exit(); // Cierra la app temporalmente
    }

    private void cargarEnFormulario(Autor a) {
        if (a == null) return;

        txtNombre.setText(a.getNombre());
        txtNacionalidad.setText(a.getNacionalidad());
        dpFechaNacimiento.setValue(a.getFechaNacimiento());
        chkActivo.setSelected(a.isActivo());
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtNacionalidad.clear();
        dpFechaNacimiento.setValue(LocalDate.now());
        chkActivo.setSelected(true);
    }

    private void refrescarDatos() {
        autoresObs.setAll(service.getAutores());
    }

    private void info(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    private void error(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}
