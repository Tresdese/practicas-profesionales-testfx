package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import logic.DAO.ProjectRequestDAO;
import logic.DTO.ProjectRequestDTO;
import logic.DTO.ProjectStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class GUI_ManageProjectRequestController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_ManageProjectRequestController.class);

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
            LOGGER.error("El objeto ProjectRequestDTO es nulo.");
            statusLabel.setText("Error: El objeto de solicitud de proyecto es nulo.");
            statusLabel.setTextFill(Color.RED);
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
                statusLabel.setTextFill(Color.GREEN);

                if (parentController != null) {
                    parentController.loadRequestData();
                }
            } else {
                statusLabel.setText("No se pudo actualizar la solicitud de proyecto.");
                statusLabel.setTextFill(Color.RED);
            }

        } catch (NumberFormatException e) {
            statusLabel.setText("Los campos numéricos deben contener solo números.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error de formato numérico: {}", e.getMessage(), e);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if ("42S02".equals(sqlState)) {
                statusLabel.setText("Tabla no encontrada en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if ("42S22".equals(sqlState)) {
                statusLabel.setText("Columna no encontrada en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if ("HY000".equals(sqlState)) {
                statusLabel.setText("Error general de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad. Verifique los datos ingresados.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al actualizar la solicitud de proyecto.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al actualizar la solicitud de proyecto: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al cargar la configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error de entrada/salida: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Ocurrió un error inesperado.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado: {}", e.getMessage(), e);
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