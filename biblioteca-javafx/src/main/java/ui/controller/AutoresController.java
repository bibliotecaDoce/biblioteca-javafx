package ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Autor;
import service.BibliotecaService;
import util.Validations;

import java.time.LocalDate;

public class AutoresController {

    // Formulario
    @FXML private TextField txtNombre;
    @FXML private TextField txtNacionalidad;

    @FXML private DatePicker dpFechaNacimiento;
    @FXML private CheckBox chkActivo;

    // ListView
    @FXML private ListView<Autor> lstAutores;

    // TableView
    @FXML private TableView<Autor> tblAutores;
    @FXML private TableColumn<Autor, Integer> colId;
    @FXML private TableColumn<Autor, String> colNombre;
    @FXML private TableColumn<Autor, String> colNacionalidad;
    @FXML private TableColumn<Autor, LocalDate> colFechaNacimiento;
    @FXML private TableColumn<Autor, Boolean> colActivo;

    // Botones
    @FXML private Button btnEliminar;
    @FXML private Button btnToggleTabla;
    @FXML private Button btnEditar;

    private final BibliotecaService service = new BibliotecaService();
    private final ObservableList<Autor> autoresObs = FXCollections.observableArrayList();
    private boolean tablaVisible = false;

    @FXML
    public void initialize() {

        // Columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNacionalidad.setCellValueFactory(new PropertyValueFactory<>("nacionalidad"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        tblAutores.setItems(autoresObs);
        lstAutores.setItems(autoresObs);

        // Texto de ListView (incluye fecha y activo)
        lstAutores.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Autor item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String fecha = (item.getFechaNacimiento() != null) ? item.getFechaNacimiento().toString() : "-";
                    String activoTxt = item.isActivo() ? "Sí" : "No";
                    setText(item.getId() + " - " + item.getNombre()
                            + " | " + item.getNacionalidad()
                            + " | nac: " + fecha
                            + " | activo: " + activoTxt);
                }
            }
        });

        refreshData();
        setTablaVisible(false);

        // Botones protegidos
        btnEliminar.setDisable(true);
        btnEditar.setDisable(true);

        // Selección ListView
        lstAutores.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                tblAutores.getSelectionModel().clearSelection();
                rellenarFormulario(sel);
                btnEliminar.setDisable(false);
                btnEditar.setDisable(false);
            }
        });

        // Selección TableView
        tblAutores.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                lstAutores.getSelectionModel().clearSelection();
                rellenarFormulario(sel);
                btnEliminar.setDisable(false);
                btnEditar.setDisable(false);
            }
        });

        // UX: Enter en nacionalidad -> guardar
        txtNacionalidad.setOnAction(e -> onGuardar(null));

        // Defaults para nuevos autores
        chkActivo.setSelected(true);
        dpFechaNacimiento.setValue(null);
    }

    private void refreshData() {
        autoresObs.setAll(service.getAutores());
    }

    private void rellenarFormulario(Autor a) {
        txtNombre.setText(a.getNombre());
        txtNacionalidad.setText(a.getNacionalidad());
        dpFechaNacimiento.setValue(a.getFechaNacimiento()); // puede ser null
        chkActivo.setSelected(a.isActivo());
    }

    private void setTablaVisible(boolean visible) {
        tablaVisible = visible;
        tblAutores.setVisible(visible);
        tblAutores.setManaged(visible);
        btnToggleTabla.setText(visible ? "Ocultar tabla" : "Ver tabla");
    }

    private Autor getSeleccionado() {
        Autor sel = lstAutores.getSelectionModel().getSelectedItem();
        if (sel == null) sel = tblAutores.getSelectionModel().getSelectedItem();
        return sel;
    }

    // ---------------- Actions ----------------

    @FXML
    public void onToggleTabla(ActionEvent event) {
        setTablaVisible(!tablaVisible);
    }

    @FXML
    public void onNuevo(ActionEvent event) {
        txtNombre.clear();
        txtNacionalidad.clear();
        dpFechaNacimiento.setValue(null);
        chkActivo.setSelected(true);

        txtNombre.requestFocus();

        lstAutores.getSelectionModel().clearSelection();
        tblAutores.getSelectionModel().clearSelection();

        btnEliminar.setDisable(true);
        btnEditar.setDisable(true);
    }

    @FXML
    public void onGuardar(ActionEvent event) {
        String nombre = txtNombre.getText();
        String nacionalidad = txtNacionalidad.getText();
        LocalDate fechaNac = dpFechaNacimiento.getValue();
        boolean activo = chkActivo.isSelected();

        if (Validations.isBlank(nombre) || Validations.isBlank(nacionalidad)) {
            error("Nombre y nacionalidad son obligatorios.");
            return;
        }
        if (fechaNac == null) {
            error("La fecha de nacimiento es obligatoria.");
            return;
        }
        if (fechaNac.isAfter(LocalDate.now())) {
            error("La fecha de nacimiento no puede ser futura.");
            return;
        }

        Autor a = new Autor(0, nombre.trim(), nacionalidad.trim(), fechaNac, activo);
        service.addAutor(a);

        refreshData();
        info("Autor guardado (ID: " + a.getId() + ")");
        onNuevo(null);
    }

    @FXML
    public void onEditar(ActionEvent event) {
        Autor sel = getSeleccionado();
        if (sel == null) {
            error("Selecciona un autor para editar.");
            return;
        }

        String nombre = txtNombre.getText();
        String nacionalidad = txtNacionalidad.getText();
        LocalDate fechaNac = dpFechaNacimiento.getValue();
        boolean activo = chkActivo.isSelected();

        if (Validations.isBlank(nombre) || Validations.isBlank(nacionalidad)) {
            error("Nombre y nacionalidad no pueden estar vacíos.");
            return;
        }
        if (fechaNac == null) {
            error("La fecha de nacimiento es obligatoria.");
            return;
        }
        if (fechaNac.isAfter(LocalDate.now())) {
            error("La fecha de nacimiento no puede ser futura.");
            return;
        }

        // Actualizamos el seleccionado
        sel.setNombre(nombre.trim());
        sel.setNacionalidad(nacionalidad.trim());
        sel.setFechaNacimiento(fechaNac);
        sel.setActivo(activo);

        service.updateAutor();

        refreshData();
        info("Autor actualizado (ID: " + sel.getId() + ")");
        onNuevo(null);
    }

    @FXML
    public void onEliminar(ActionEvent event) {
        Autor sel = getSeleccionado();
        if (sel == null) {
            error("Selecciona un autor para eliminar.");
            return;
        }

        final Autor autorAEliminar = sel;

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Eliminar el autor seleccionado (ID " + autorAEliminar.getId() + ")?",
                ButtonType.OK,
                ButtonType.CANCEL
        );

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                service.deleteAutor(autorAEliminar);
                refreshData();
                onNuevo(null);
                info("Autor eliminado.");
            }
        });
    }

    @FXML
    public void onVolver(ActionEvent event) {
        ((javafx.scene.Node) event.getSource()).getScene().getWindow().hide();
    }

    // ---------------- Alerts ----------------

    private void info(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    private void error(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}
