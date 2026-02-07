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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import model.Autor;
import service.BibliotecaService;
import util.Validations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

public class AutoresController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtNacionalidad;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private CheckBox chkActivo;

    // Debe coincidir con autores.fxml: fx:id="lstAutores"
    @FXML private ListView<Autor> lstAutores;

    @FXML private TableView<Autor> tblAutores;
    @FXML private TableColumn<Autor, Integer> colId;
    @FXML private TableColumn<Autor, String> colNombre;
    @FXML private TableColumn<Autor, String> colNacionalidad;
    @FXML private TableColumn<Autor, LocalDate> colFechaNacimiento;
    @FXML private TableColumn<Autor, Boolean> colActivo;

    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnToggleTabla;

    private final BibliotecaService service = new BibliotecaService();
    private final ObservableList<Autor> autoresObs = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        // Datos compartidos
        lstAutores.setItems(autoresObs);
        tblAutores.setItems(autoresObs);

        // ListView (texto)
        lstAutores.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Autor a, boolean empty) {
                super.updateItem(a, empty);
                if (empty || a == null) {
                    setText(null);
                } else {
                    String fecha = (a.getFechaNacimiento() != null) ? a.getFechaNacimiento().toString() : "-";
                    setText(a.getId() + " - " + a.getNombre()
                            + " | " + a.getNacionalidad()
                            + " | nac: " + fecha
                            + " | activo: " + (a.isActivo() ? "Sí" : "No"));
                }
            }
        });

        // TableView
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colNacionalidad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNacionalidad()));
        colFechaNacimiento.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getFechaNacimiento()));
        colActivo.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isActivo()).asObject());

        // Tabla oculta al inicio (también controlado por FXML, pero lo reforzamos)
        tblAutores.setVisible(false);
        tblAutores.setManaged(false);

        // Botones protegidos
        if (btnEditar != null) btnEditar.setDisable(true);
        if (btnEliminar != null) btnEliminar.setDisable(true);

        // Selección ListView
        lstAutores.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                tblAutores.getSelectionModel().clearSelection();
                cargarEnFormulario(sel);
                if (btnEditar != null) btnEditar.setDisable(false);
                if (btnEliminar != null) btnEliminar.setDisable(false);
            }
        });

        // Selección TableView
        tblAutores.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                lstAutores.getSelectionModel().clearSelection();
                cargarEnFormulario(sel);
                if (btnEditar != null) btnEditar.setDisable(false);
                if (btnEliminar != null) btnEliminar.setDisable(false);
            }
        });

        // Defaults
        chkActivo.setSelected(true);
        dpFechaNacimiento.setValue(null);

        refrescarDatos();
    }

    @FXML
    public void onNuevo(ActionEvent event) {
        lstAutores.getSelectionModel().clearSelection();
        tblAutores.getSelectionModel().clearSelection();
        limpiarFormulario();
        if (btnEditar != null) btnEditar.setDisable(true);
        if (btnEliminar != null) btnEliminar.setDisable(true);
        txtNombre.requestFocus();
    }

    @FXML
    public void onGuardar(ActionEvent event) {
        String nombre = (txtNombre.getText() == null) ? "" : txtNombre.getText().trim();
        String nacionalidad = (txtNacionalidad.getText() == null) ? "" : txtNacionalidad.getText().trim();
        LocalDate fecha = dpFechaNacimiento.getValue();
        boolean activo = chkActivo.isSelected();

        if (Validations.isBlank(nombre) || Validations.isBlank(nacionalidad) || fecha == null) {
            error("Todos los campos son obligatorios.");
            return;
        }

        // PR2: error si contiene números
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
        if (btnEditar != null) btnEditar.setDisable(true);
        if (btnEliminar != null) btnEliminar.setDisable(true);
        info("Autor guardado (ID: " + a.getId() + ")");
    }

    @FXML
    public void onEditar(ActionEvent event) {
        Autor sel = getSeleccionado();
        if (sel == null) {
            error("Selecciona un autor para editar.");
            return;
        }

        String nombre = (txtNombre.getText() == null) ? "" : txtNombre.getText().trim();
        String nacionalidad = (txtNacionalidad.getText() == null) ? "" : txtNacionalidad.getText().trim();
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
        info("Autor actualizado.");
    }

    @FXML
    public void onEliminar(ActionEvent event) {
        Autor sel = getSeleccionado();
        if (sel == null) {
            error("Selecciona un autor para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar el autor seleccionado (ID " + sel.getId() + ")?",
                ButtonType.OK, ButtonType.CANCEL);

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                service.deleteAutor(sel);
                refrescarDatos();
                limpiarFormulario();
                if (btnEditar != null) btnEditar.setDisable(true);
                if (btnEliminar != null) btnEliminar.setDisable(true);
                info("Autor eliminado.");
            }
        });
    }

    @FXML
    public void onToggleTabla(ActionEvent event) {
        boolean visible = !tblAutores.isVisible();
        tblAutores.setVisible(visible);
        tblAutores.setManaged(visible);
        if (btnToggleTabla != null) {
            btnToggleTabla.setText(visible ? "View table" : "View table");
        }
    }

    // EXPORTAR: copia data/autores.json a donde el usuario elija
    @FXML
    public void onExportar(ActionEvent event) {
        Path origen = Path.of("data", "autores.json");

        if (!Files.exists(origen)) {
            error("No existe el fichero data/autores.json todavía. Guarda algún autor primero.");
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Exportar autores (JSON)");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON (*.json)", "*.json"));
        fc.setInitialFileName("autores.json");

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
        // Si estáis usando SceneRouter, cambiáis esto por SceneRouter.go("menu.fxml");
        SceneRouter.go("Menu.fxml");
    }

    private Autor getSeleccionado() {
        Autor sel = lstAutores.getSelectionModel().getSelectedItem();
        if (sel == null) sel = tblAutores.getSelectionModel().getSelectedItem();
        return sel;
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


