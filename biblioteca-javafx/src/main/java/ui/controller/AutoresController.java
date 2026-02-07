package ui.controller;

import app.SceneRouter;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
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

    // OJO: debe llamarse igual que en el FXML: lstAutores
    @FXML private ListView<Autor> lstAutores;

    @FXML private TableView<Autor> tblAutores;
    @FXML private TableColumn<Autor, Integer> colId;
    @FXML private TableColumn<Autor, String> colNombre;
    @FXML private TableColumn<Autor, String> colNacionalidad;
    @FXML private TableColumn<Autor, LocalDate> colFechaNacimiento;
    @FXML private TableColumn<Autor, Boolean> colActivo;

    private final BibliotecaService service = new BibliotecaService();
    private final ObservableList<Autor> autoresObs = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        // ListView
        lstAutores.setItems(autoresObs);
        lstAutores.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Autor a, boolean empty) {
                super.updateItem(a, empty);
                if (empty || a == null) {
                    setText(null);
                } else {
                    String fecha = (a.getFechaNacimiento() != null) ? a.getFechaNacimiento().toString() : "-";
                    setText(a.getNombre() + " | " + a.getNacionalidad()
                            + " | " + fecha
                            + " | Activo: " + (a.isActivo() ? "Sí" : "No"));
                }
            }
        });

        // Tabla
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colNacionalidad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNacionalidad()));
        colFechaNacimiento.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getFechaNacimiento()));
        colActivo.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isActivo()).asObject());

        tblAutores.setItems(autoresObs);

        // Selección desde ListView
        lstAutores.getSelectionModel().selectedItemProperty().addListener((obs, o, sel) -> cargarEnFormulario(sel));

        refrescarDatos();
    }

    @FXML
    public void onNuevo() {
        lstAutores.getSelectionModel().clearSelection();
        limpiarFormulario();
    }

    @FXML
    public void onGuardar() {
        String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        String nacionalidad = txtNacionalidad.getText() == null ? "" : txtNacionalidad.getText().trim();
        LocalDate fecha = dpFechaNacimiento.getValue();
        boolean activo = chkActivo.isSelected();

        if (Validations.isBlank(nombre) || Validations.isBlank(nacionalidad) || fecha == null) {
            error("Todos los campos son obligatorios.");
            return;
        }

        // Validación PR2: no permitir números
        if (nombre.matches(".*\\d.*")) {
            error("El nombre no puede contener números.");
            return;
        }

        if (fecha.isAfter(LocalDate.now())) {
            error("La fecha de nacimiento no puede ser futura.");
            return;
        }

        Autor a = new Autor(0, nombre, nacionalidad, fecha, activo);
        service.addAutor(a);
        refrescarDatos();
        limpiarFormulario();
        info("Autor guardado (ID: " + a.getId() + ")");
    }

    // OJO: ahora se llama onEditar para coincidir con el FXML
    @FXML
    public void onEditar() {
        Autor sel = lstAutores.getSelectionModel().getSelectedItem();
        if (sel == null) {
            error("Selecciona un autor para editar.");
            return;
        }

        String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        String nacionalidad = txtNacionalidad.getText() == null ? "" : txtNacionalidad.getText().trim();
        LocalDate fecha = dpFechaNacimiento.getValue();

        if (Validations.isBlank(nombre) || Validations.isBlank(nacionalidad) || fecha == null) {
            error("Todos los campos son obligatorios.");
            return;
        }

        if (nombre.matches(".*\\d.*")) {
            error("El nombre no puede contener números.");
            return;
        }

        if (fecha.isAfter(LocalDate.now())) {
            error("La fecha de nacimiento no puede ser futura.");
            return;
        }

        sel.setNombre(nombre);
        sel.setNacionalidad(nacionalidad);
        sel.setFechaNacimiento(fecha);
        sel.setActivo(chkActivo.isSelected());

        service.updateAutor(sel);
        refrescarDatos();
        info("Autor modificado.");
    }

    @FXML
    public void onEliminar() {
        Autor sel = lstAutores.getSelectionModel().getSelectedItem();
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
    public void onToggleTabla() {
        boolean visible = !tblAutores.isVisible();
        tblAutores.setVisible(visible);
        tblAutores.setManaged(visible);
    }

    @FXML
    public void onVolver() {
        // Si ya tenéis menú, lo correcto es volver al menú con SceneRouter.
        // Si no, esto cierra la app.
        SceneRouter.go("menu.fxml");
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
        dpFechaNacimiento.setValue(null);
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
