package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DAO.AssessmentCriterionDAO;
import logic.DTO.AssessmentCriterionDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class GUI_ManageAssessmentCriteriaController {

    private static final Logger logger = LogManager.getLogger(GUI_ManageAssessmentCriteriaController.class);

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
            setStatus("Error al cargar criterios.", true);
            logger.error("Error SQL al cargar criterios: {}", e.getMessage(), e);
        } catch (Exception e) {
            setStatus("Error inesperado al cargar criterios.", true);
            logger.error("Error inesperado al cargar criterios: {}", e.getMessage(), e);
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
            setStatus("Error de base de datos al registrar.", true);
            logger.error("Error SQL al registrar criterio: {}", e.getMessage(), e);
        } catch (Exception e) {
            setStatus(e.getMessage(), true);
            logger.error("Error al registrar criterio: {}", e.getMessage(), e);
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
            setStatus("Error de base de datos al eliminar.", true);
            logger.error("Error SQL al eliminar criterio: {}", e.getMessage(), e);
        } catch (Exception e) {
            setStatus(e.getMessage(), true);
            logger.error("Error al eliminar criterio: {}", e.getMessage(), e);
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
            setStatus("Error de base de datos al actualizar.", true);
            logger.error("Error SQL al actualizar criterio: {}", e.getMessage(), e);
        } catch (Exception e) {
            setStatus(e.getMessage(), true);
            logger.error("Error al actualizar criterio: {}", e.getMessage(), e);
        }
    }

    private boolean areFieldsFilled() {
        return !idCriterionField.getText().trim().isEmpty() && !nameCriterionField.getText().trim().isEmpty();
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setTextFill(isError ? javafx.scene.paint.Color.RED : javafx.scene.paint.Color.GREEN);
    }

    private void clearStatus() {
        statusLabel.setText("");
    }
}