package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import logic.DAO.ActivityDAO;
import logic.DTO.ActivityDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class GUI_ManageActivityController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_ManageActivityController.class);

    @FXML
    private TextField idActivityField;
    @FXML
    private TextField nameActivityField;
    @FXML
    private Button registerButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button updateButton;
    @FXML
    private Label statusLabel;
    @FXML
    private TableView<ActivityDTO> activityTable;
    @FXML
    private TableColumn<ActivityDTO, String> idColumn;
    @FXML
    private TableColumn<ActivityDTO, String> nameColumn;

    private final ActivityDAO activityDAO = new ActivityDAO();
    private final ObservableList<ActivityDTO> activityList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getActivityId()));
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getActivityName()));
        activityTable.setItems(activityList);
        loadActivities();
        activityTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillFields(newSel));
    }

    private void loadActivities() {
        try {
            activityList.setAll(activityDAO.getAllActivities());
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                setStatus("Error de conexión con la base de datos.", true);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                setStatus("Error de conexión interrumpida con la base de datos.", true);
                LOGGER.error("Error de conexion interrumpida  con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                setStatus("Base de datos desconocida.", true);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                setStatus("Acceso denegado a la base de datos.", true);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                setStatus("Error al cargar actividades: ", true);
                LOGGER.error("Error al cargar actividades: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            setStatus("Error al cargar actividades: ", true);
            LOGGER.error("Error al cargar actividades: {}", e.getMessage(), e);
        }
    }

    private void fillFields(ActivityDTO activity) {
        if (activity != null) {
            idActivityField.setText(activity.getActivityId());
            nameActivityField.setText(activity.getActivityName());
            idActivityField.setDisable(true);
        }
    }

    @FXML
    private void handleRegister() {
        clearStatus();
        try {
            if (!areFieldsFilled()) {
                throw new IllegalArgumentException("Todos los campos son obligatorios.");
            }
            ActivityDTO activity = new ActivityDTO(idActivityField.getText().trim(), nameActivityField.getText().trim());
            boolean inserted = activityDAO.insertActivity(activity);
            if (inserted) {
                setStatus("Actividad registrada correctamente.", false);
                loadActivities();
                handleClear();
            } else {
                setStatus("No se pudo registrar la actividad.", true);
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                setStatus("Error de conexión con la base de datos.", true);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                setStatus("Error de conexión interrumpida con la base de datos.", true);
                LOGGER.error("Error de conexion interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                setStatus("Base de datos desconocida.", true);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                setStatus("Acceso denegado a la base de datos.", true);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("23000")) {
                setStatus("Violación de restricción de integridad.", true);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                setStatus("Error de base de datos al registrar.", true);
                LOGGER.error("Error SQL al registrar actividad: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            setStatus("Error inesperado al registrar actividad", true);
            LOGGER.error("Error al registrar actividad: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleClear() {
        idActivityField.clear();
        nameActivityField.clear();
        idActivityField.setDisable(false);
        statusLabel.setText("");
        activityTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleDelete() {
        clearStatus();
        ActivityDTO selected = activityTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("Seleccione una actividad para eliminar.", true);
            return;
        }
        try {
            boolean deleted = activityDAO.deleteActivity(selected);
            if (deleted) {
                setStatus("Actividad eliminada correctamente.", false);
                loadActivities();
                handleClear();
            } else {
                setStatus("No se pudo eliminar la actividad.", true);
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                setStatus("Error de conexión con la base de datos.", true);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                setStatus("Error de conexión interrumpida con la base de datos.", true);
                LOGGER.error("Error de conexion interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                setStatus("Base de datos desconocida.", true);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                setStatus("Acceso denegado a la base de datos.", true);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("23000")) {
                setStatus("Violación de restricción de integridad.", true);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                setStatus("Error de base de datos al eliminar.", true);
                LOGGER.error("Error SQL al eliminar actividad: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            setStatus("Error inesperado al eliminar actividad", true);
            LOGGER.error("Error al eliminar actividad: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleUpdate() {
        clearStatus();
        try {
            if (!areFieldsFilled()) {
                throw new IllegalArgumentException("Todos los campos son obligatorios.");
            }
            ActivityDTO activity = new ActivityDTO(idActivityField.getText().trim(), nameActivityField.getText().trim());
            boolean updated = activityDAO.updateActivity(activity);
            if (updated) {
                setStatus("Actividad actualizada correctamente.", false);
                loadActivities();
                handleClear();
            } else {
                setStatus("No se pudo actualizar la actividad.", true);
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                setStatus("Error de conexión con la base de datos.", true);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                setStatus("Error de conexión interrumpida con la base de datos.", true);
                LOGGER.error("Error de conexion interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                setStatus("Base de datos desconocida.", true);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                setStatus("Acceso denegado a la base de datos.", true);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("23000")) {
                setStatus("Violación de restricción de integridad.", true);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                setStatus("Error de base de datos al actualizar.", true);
                LOGGER.error("Error SQL al actualizar actividad: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            setStatus("Error inesperado al actualizar actividad", true);
            LOGGER.error("Error al actualizar actividad: {}", e.getMessage(), e);
        }
    }

    private boolean areFieldsFilled() {
        return !idActivityField.getText().trim().isEmpty() && !nameActivityField.getText().trim().isEmpty();
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setTextFill(isError ? Color.RED : Color.GREEN);
    }

    private void clearStatus() {
        statusLabel.setText("");
    }
}