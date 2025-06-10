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
    private TextField fieldTuiton, fieldOrganizationId, fieldProjectId, fieldRepresentativeId;

    @FXML
    private TextField fieldDuration, fieldScheduleDays, fieldDirectUsers, fieldIndirectUsers;

    @FXML
    private TextArea fieldDescription, fieldGeneralObjective;

    @FXML
    private ComboBox<String> comboStatus;

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
        comboStatus.setItems(FXCollections.observableArrayList("pendiente", "aprobada", "rechazada"));

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

        fieldTuiton.setText(projectRequest.getTuiton());
        fieldOrganizationId.setText(String.valueOf(projectRequest.getOrganizationId()));
        fieldProjectId.setText(projectRequest.getProjectName());
        fieldRepresentativeId.setText(String.valueOf(projectRequest.getRepresentativeId()));
        fieldDescription.setText(projectRequest.getDescription());
        fieldGeneralObjective.setText(projectRequest.getGeneralObjective());
        fieldDuration.setText(String.valueOf(projectRequest.getDuration()));
        fieldScheduleDays.setText(projectRequest.getScheduleDays());
        fieldDirectUsers.setText(String.valueOf(projectRequest.getDirectUsers()));
        fieldIndirectUsers.setText(String.valueOf(projectRequest.getIndirectUsers()));
        comboStatus.setValue(projectRequest.getStatus() != null ? projectRequest.getStatus().name() : "pendiente");
    }

    @FXML
    private void handleSaveChanges() {
        try {
            if (!areFieldsFilled()) {
                throw new IllegalArgumentException("Todos los campos obligatorios deben estar llenos.");
            }

            projectRequest.setTuiton(fieldTuiton.getText());
            projectRequest.setOrganizationId(fieldOrganizationId.getText());
            projectRequest.setProjectName(fieldProjectId.getText());
            projectRequest.setRepresentativeId(fieldRepresentativeId.getText());
            projectRequest.setDescription(fieldDescription.getText());
            projectRequest.setGeneralObjective(fieldGeneralObjective.getText());
            projectRequest.setDuration(Integer.parseInt(fieldDuration.getText()));
            projectRequest.setScheduleDays(fieldScheduleDays.getText());
            projectRequest.setDirectUsers(Integer.parseInt(fieldDirectUsers.getText()));
            projectRequest.setIndirectUsers(Integer.parseInt(fieldIndirectUsers.getText()));
            projectRequest.setStatus(ProjectStatus.valueOf(comboStatus.getValue()));

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
        return !fieldTuiton.getText().isEmpty() &&
                !fieldOrganizationId.getText().isEmpty() &&
                !fieldProjectId.getText().isEmpty() &&
                !fieldRepresentativeId.getText().isEmpty() &&
                !fieldGeneralObjective.getText().isEmpty() &&
                !fieldDuration.getText().isEmpty() &&
                !fieldScheduleDays.getText().isEmpty() &&
                !fieldDirectUsers.getText().isEmpty() &&
                !fieldIndirectUsers.getText().isEmpty() &&
                comboStatus.getValue() != null;
    }
}