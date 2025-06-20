package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.DAO.ProjectRequestDAO;
import logic.DTO.ProjectRequestDTO;
import logic.DTO.ProjectStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class GUI_ManageProjectRequestController {

    private static final Logger logger = LogManager.getLogger(GUI_ManageProjectRequestController.class);

    @FXML
    private TextField fieldTuition, organizationIdField, projectIdField, representativeIdField;

    @FXML
    private TextField durationField, scheduleDaysField, directUsersField, indirectUsersField;

    @FXML
    private TextArea descriptionField, generalObjectiveField;

    @FXML
    private ComboBox<String> statusCombo;

    @FXML
    private Label statusLabel;

    private GUI_CheckProjectRequestListController parentController;
    private ProjectRequestDTO projectRequest;
    private ProjectRequestDAO projectRequestDAO;

    public void setParentController(GUI_CheckProjectRequestListController parentController) {
        this.parentController = parentController;
    }

    public void setProjectRequestDAO(ProjectRequestDAO projectRequestDAO) {
        this.projectRequestDAO = projectRequestDAO;
    }

    @FXML
    public void initialize() {
        statusCombo.setItems(FXCollections.observableArrayList("pendiente", "aprobada", "rechazada"));

        if (projectRequestDAO == null) {
            projectRequestDAO = new ProjectRequestDAO();
        }
    }

    public void setProjectRequestData(ProjectRequestDTO projectRequest) {
        if (projectRequest == null) {
            logger.error("El objeto ProjectRequestDTO es nulo.");
            return;
        }

        this.projectRequest = projectRequest;

        fieldTuition.setText(projectRequest.getTuition());
        organizationIdField.setText(String.valueOf(projectRequest.getOrganizationId()));
        projectIdField.setText(projectRequest.getProjectName());
        representativeIdField.setText(String.valueOf(projectRequest.getRepresentativeId()));
        descriptionField.setText(projectRequest.getDescription());
        generalObjectiveField.setText(projectRequest.getGeneralObjective());
        durationField.setText(String.valueOf(projectRequest.getDuration()));
        scheduleDaysField.setText(projectRequest.getScheduleDays());
        directUsersField.setText(String.valueOf(projectRequest.getDirectUsers()));
        indirectUsersField.setText(String.valueOf(projectRequest.getIndirectUsers()));
        statusCombo.setValue(projectRequest.getStatus() != null ? projectRequest.getStatus().name() : "pendiente");
    }

    @FXML
    private void handleSaveChanges() {
        try {
            if (!areFieldsFilled()) {
                throw new IllegalArgumentException("Todos los campos obligatorios deben estar llenos.");
            }

            projectRequest.setTuition(fieldTuition.getText());
            projectRequest.setOrganizationId(organizationIdField.getText());
            projectRequest.setProjectName(projectIdField.getText());
            projectRequest.setRepresentativeId(representativeIdField.getText());
            projectRequest.setDescription(descriptionField.getText());
            projectRequest.setGeneralObjective(generalObjectiveField.getText());
            projectRequest.setDuration(Integer.parseInt(durationField.getText()));
            projectRequest.setScheduleDays(scheduleDaysField.getText());
            projectRequest.setDirectUsers(Integer.parseInt(directUsersField.getText()));
            projectRequest.setIndirectUsers(Integer.parseInt(indirectUsersField.getText()));
            projectRequest.setStatus(ProjectStatus.valueOf(statusCombo.getValue()));

            boolean result = projectRequestDAO.updateProjectRequest(projectRequest);

            if (result) {
                statusLabel.setText("¡Solicitud de proyecto actualizada exitosamente!");
                statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);

                if (parentController != null) {
                    parentController.loadRequestData();
                }
            } else {
                statusLabel.setText("No se pudo actualizar la solicitud de proyecto.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            }

        } catch (NumberFormatException e) {
            statusLabel.setText("Los campos numéricos deben contener solo números.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            logger.error("Error de formato numérico: {}", e.getMessage(), e);
        } catch (SQLException e) {
            statusLabel.setText("Error al actualizar los datos en la base de datos.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            logger.error("Error SQL: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            logger.error("Error: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }

    private boolean areFieldsFilled() {
        return !fieldTuition.getText().isEmpty() &&
                !organizationIdField.getText().isEmpty() &&
                !projectIdField.getText().isEmpty() &&
                !representativeIdField.getText().isEmpty() &&
                !generalObjectiveField.getText().isEmpty() &&
                !durationField.getText().isEmpty() &&
                !scheduleDaysField.getText().isEmpty() &&
                !directUsersField.getText().isEmpty() &&
                !indirectUsersField.getText().isEmpty() &&
                statusCombo.getValue() != null;
    }
}