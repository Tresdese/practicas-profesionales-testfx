package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DAO.SelfAssessmentCriteriaDAO;
import logic.DTO.SelfAssessmentCriteriaDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class GUI_ManageSelfAssessmentCriteriaController {

    @FXML
    private TextField idCriteriaField;

    @FXML
    private TextField nameCriteriaField;

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
    private TableView<SelfAssessmentCriteriaDTO> criteriaTable;

    @FXML
    private TableColumn<SelfAssessmentCriteriaDTO, String> idColumn;

    @FXML
    private TableColumn<SelfAssessmentCriteriaDTO, String> nameColumn;

    private final SelfAssessmentCriteriaDAO criteriaDAO = new SelfAssessmentCriteriaDAO();
    private final ObservableList<SelfAssessmentCriteriaDTO> criteriaList = FXCollections.observableArrayList();

    private static final Logger LOGGER = LogManager.getLogger(GUI_ManageSelfAssessmentCriteriaController.class);

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getIdCriteria()));
        nameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNameCriteria()));
        criteriaTable.setItems(criteriaList);
        loadCriteria();

        criteriaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                idCriteriaField.setText(newSel.getIdCriteria());
                nameCriteriaField.setText(newSel.getNameCriteria());
                idCriteriaField.setDisable(true);
            }
        });
    }

    @FXML
    private void handleRegister() {
        clearStatus();
        try {
            if (!areFieldsFilled()) {
                throw new IllegalArgumentException("Todos los campos son obligatorios.");
            }
            SelfAssessmentCriteriaDTO criteria = new SelfAssessmentCriteriaDTO(
                    idCriteriaField.getText().trim(),
                    nameCriteriaField.getText().trim()
            );
            boolean inserted = criteriaDAO.insertSelfAssessmentCriteria(criteria);
            if (inserted) {
                setStatus("Criterio registrado correctamente.", false);
                loadCriteria();
                clearFields();
            } else {
                setStatus("No se pudo registrar el criterio.", true);
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                setStatus("Error de conexión con la base de datos.", true);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                setStatus("Error de conexión con la base de datos.", true);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                setStatus("Base de datos desconocida.", true);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                setStatus("Acceso denegado a la base de datos.", true);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                setStatus("Violación de restricción de integridad.", true);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                setStatus("Error al registrar el criterio: ",  true);
                LOGGER.error("Error SQL al registrar criterio: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            setStatus("Error inesperado al registrar criterio", true);
            LOGGER.error("Error al registrar criterio: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
        clearStatus();
    }

    @FXML
    private void handleDelete() {
        clearStatus();
        SelfAssessmentCriteriaDTO selected = criteriaTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("Seleccione un criterio para eliminar.", true);
            return;
        }
        try {
            boolean deleted = criteriaDAO.deleteSelfAssessmentCriteria(selected);
            if (deleted) {
                setStatus("Criterio eliminado correctamente.", false);
                loadCriteria();
                clearFields();
            } else {
                setStatus("No se pudo eliminar el criterio.", true);
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                setStatus("Error de conexión con la base de datos.", true);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                setStatus("Error de conexión con la base de datos.", true);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                setStatus("Base de datos desconocida.", true);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                setStatus("Acceso denegado a la base de datos.", true);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                setStatus("Violación de restricción de integridad.", true);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                setStatus("Error al eliminar el criterio: ", true);
                LOGGER.error("Error SQL al eliminar criterio: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            setStatus("Error inesperado al eliminar criterio", true);
            LOGGER.error("Error al eliminar criterio: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleUpdate() {
        clearStatus();
        try {
            if (!areFieldsFilled()) {
                throw new IllegalArgumentException("Todos los campos son obligatorios.");
            }
            SelfAssessmentCriteriaDTO criteria = new SelfAssessmentCriteriaDTO(
                    idCriteriaField.getText().trim(),
                    nameCriteriaField.getText().trim()
            );
            boolean updated = criteriaDAO.updateSelfAssessmentCriteria(criteria);
            if (updated) {
                setStatus("Criterio actualizado correctamente.", false);
                loadCriteria();
                clearFields();
            } else {
                setStatus("No se pudo actualizar el criterio.", true);
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                setStatus("Error de conexión con la base de datos.", true);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                setStatus("Error de conexión con la base de datos.", true);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                setStatus("Base de datos desconocida.", true);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                setStatus("Acceso denegado a la base de datos.", true);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                setStatus("Violación de restricción de integridad.", true);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                setStatus("Error al actualizar el criterio: ", true);
                LOGGER.error("Error SQL al actualizar criterio: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            setStatus("Error inesperado al actualizar criterio ", true);
            LOGGER.error("Error inesperado al actualizar criterio: {}", e.getMessage(), e);
        }
    }

    private void loadCriteria() {
        try {
            List<SelfAssessmentCriteriaDTO> allCriteria = criteriaDAO.getAllSelfAssessmentCriteria();
            criteriaList.setAll(allCriteria);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                setStatus("Error de conexión con la base de datos.", true);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                setStatus("Error de conexión con la base de datos.", true);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                setStatus("Base de datos desconocida.", true);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                setStatus("Acceso denegado a la base de datos.", true);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                setStatus("Violación de restricción de integridad.", true);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                setStatus("Error al cargar los criterios: ", true);
                LOGGER.error("Error SQL al cargar criterios: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            setStatus("Error al cargar los criterios: " + e.getMessage(), true);
            LOGGER.error("Error al cargar criterios: {}", e.getMessage(), e);
        }
    }

    private boolean areFieldsFilled() {
        return !idCriteriaField.getText().trim().isEmpty() && !nameCriteriaField.getText().trim().isEmpty();
    }

    private void clearFields() {
        idCriteriaField.clear();
        nameCriteriaField.clear();
        idCriteriaField.setDisable(false);
        criteriaTable.getSelectionModel().clearSelection();
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setTextFill(isError ? javafx.scene.paint.Color.RED : javafx.scene.paint.Color.GREEN);
    }

    private void clearStatus() {
        statusLabel.setText("");
    }
}