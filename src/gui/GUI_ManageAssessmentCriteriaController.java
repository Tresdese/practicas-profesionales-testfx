package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import logic.DAO.AssessmentCriterionDAO;
import logic.DTO.AssessmentCriterionDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class GUI_ManageAssessmentCriteriaController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_ManageAssessmentCriteriaController.class);

    @FXML
    private TextField idCriterionField;
    @FXML
    private TextField nameCriterionField;
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
    private TableView<AssessmentCriterionDTO> criteriaTable;
    @FXML
    private TableColumn<AssessmentCriterionDTO, String> idColumn;
    @FXML
    private TableColumn<AssessmentCriterionDTO, String> nameColumn;

    private final AssessmentCriterionDAO dao = new AssessmentCriterionDAO();
    private final ObservableList<AssessmentCriterionDTO> criteriaList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getIdCriterion()));
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNameCriterion()));
        criteriaTable.setItems(criteriaList);
        loadCriteria();
        criteriaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillFields(newSel));
    }

    private void loadCriteria() {
        try {
            criteriaList.setAll(dao.getAllAssessmentCriteria());
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                setStatus("Error de conexión con la base de datos.", true);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                setStatus("Error de conexión interrumpida con la base de datos.", true);
                LOGGER.error("Error de conexion interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                setStatus("Tabla de criterios no encontrada.", true);
                LOGGER.error("Tabla de criterios no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                setStatus("Columna de criterios no encontrada.", true);
                LOGGER.error("Columna de criterios no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("HY000")) {
                setStatus("Error general de la base de datos.", true);
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                setStatus("Base de datos desconocida.", true);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                setStatus("Acceso denegado a la base de datos.", true);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                setStatus("Error al cargar criterios.", true);
                LOGGER.error("Error al cargar criterios: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            setStatus("Error al cargar la configuración de la base de datos.", true);
            LOGGER.error("Error de entrada/salida al cargar criterios: {}", e.getMessage(), e);
        } catch (Exception e) {
            setStatus("Error inesperado al cargar criterios.", true);
            LOGGER.error("Error inesperado al cargar criterios: {}", e.getMessage(), e);
        }
    }

    private void fillFields(AssessmentCriterionDTO criterion) {
        if (criterion != null) {
            idCriterionField.setText(criterion.getIdCriterion());
            nameCriterionField.setText(criterion.getNameCriterion());
            idCriterionField.setDisable(true);
        }
    }

    @FXML
    private void handleRegister() {
        clearStatus();
        try {
            if (!areFieldsFilled()) {
                throw new IllegalArgumentException("Todos los campos son obligatorios.");
            }
            AssessmentCriterionDTO criterion = new AssessmentCriterionDTO(
                    idCriterionField.getText().trim(),
                    nameCriterionField.getText().trim()
            );
            boolean inserted = dao.insertAssessmentCriterion(criterion);
            if (inserted) {
                setStatus("Criterio registrado.", false);
                loadCriteria();
                handleClear();
            } else {
                setStatus("No se pudo registrar.", true);
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                setStatus("Error de conexión con la base de datos.", true);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                setStatus("Error de conexión interrumpida con la base de datos.", true);
                LOGGER.error("Error de conexion interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                setStatus("Tabla de criterios no encontrada.", true);
                LOGGER.error("Tabla de criterios no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                setStatus("Columna de criterios no encontrada.", true);
                LOGGER.error("Columna de criterios no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("HY000")) {
                setStatus("Error general de la base de datos.", true);
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
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
                LOGGER.error("Error SQL al registrar criterio: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            setStatus("Error al cargar la configuración de la base de datos.", true);
            LOGGER.error("Error de entrada/salida al registrar criterio: {}", e.getMessage(), e);
        } catch (Exception e) {
            setStatus("Error inesperado al registrar criterio", true);
            LOGGER.error("Error al registrar criterio: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleClear() {
        idCriterionField.clear();
        nameCriterionField.clear();
        idCriterionField.setDisable(false);
        statusLabel.setText("");
        criteriaTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleDelete() {
        clearStatus();
        AssessmentCriterionDTO selected = criteriaTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("Seleccione un criterio para eliminar.", true);
            return;
        }
        try {
            boolean deleted = dao.deleteAssessmentCriterion(selected.getIdCriterion());
            if (deleted) {
                setStatus("Criterio eliminado.", false);
                loadCriteria();
                handleClear();
            } else {
                setStatus("No se pudo eliminar.", true);
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                setStatus("Error de conexión con la base de datos.", true);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                setStatus("Error de conexión interrumpida con la base de datos.", true);
                LOGGER.error("Error de conexion interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                setStatus("Tabla de criterios no encontrada.", true);
                LOGGER.error("Tabla de criterios no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                setStatus("Columna de criterios no encontrada.", true);
                LOGGER.error("Columna de criterios no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("HY000")) {
                setStatus("Error general de la base de datos.", true);
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
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
                LOGGER.error("Error SQL al eliminar criterio: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            setStatus("Error al cargar la configuración de la base de datos.", true);
            LOGGER.error("Error de entrada/salida al eliminar criterio: {}", e.getMessage(), e);
        } catch (Exception e) {
            setStatus("Error inesperado al eliminar criterio", true);
            LOGGER.error("Error al eliminar criterio: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleUpdate() {
        clearStatus();
        AssessmentCriterionDTO selected = criteriaTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("Seleccione un criterio para actualizar.", true);
            return;
        }
        try {
            if (!areFieldsFilled()) {
                throw new IllegalArgumentException("Todos los campos son obligatorios.");
            }
            AssessmentCriterionDTO updated = new AssessmentCriterionDTO(
                    idCriterionField.getText().trim(),
                    nameCriterionField.getText().trim()
            );
            boolean updatedResult = dao.updateAssessmentCriterion(updated);
            if (updatedResult) {
                setStatus("Criterio actualizado.", false);
                loadCriteria();
                handleClear();
            } else {
                setStatus("No se pudo actualizar.", true);
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                setStatus("Error de conexión con la base de datos.", true);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                setStatus("Error de conexión interrumpida con la base de datos.", true);
                LOGGER.error("Error de conexion interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                setStatus("Tabla de criterios no encontrada.", true);
                LOGGER.error("Tabla de criterios no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                setStatus("Columna de criterios no encontrada.", true);
                LOGGER.error("Columna de criterios no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("HY000")) {
                setStatus("Error general de la base de datos.", true);
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
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
                LOGGER.error("Error SQL al actualizar criterio: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            setStatus("Error al cargar la configuración de la base de datos.", true);
            LOGGER.error("Error de entrada/salida al actualizar criterio: {}", e.getMessage(), e);
        } catch (Exception e) {
            setStatus("Error inesperado al actualizar criterio", true);
            LOGGER.error("Error al actualizar criterio: {}", e.getMessage(), e);
        }
    }

    private boolean areFieldsFilled() {
        return !idCriterionField.getText().trim().isEmpty() && !nameCriterionField.getText().trim().isEmpty();
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setTextFill(isError ? Color.RED : Color.GREEN);
    }

    private void clearStatus() {
        statusLabel.setText("");
    }
}