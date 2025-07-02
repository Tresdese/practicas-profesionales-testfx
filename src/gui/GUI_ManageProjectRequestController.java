package gui;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

    @FXML
    private Button saveButton, cancelButton;

    private GUI_CheckProjectRequestListController parentController;
    private ProjectRequestDTO projectRequest;
    private ProjectRequestDAO projectRequestDAO;

    private String originalTuition = "";
    private String originalOrganizationId = "";
    private String originalProjectId = "";
    private String originalRepresentativeId = "";
    private String originalDescription = "";
    private String originalGeneralObjective = "";
    private String originalDuration = "";
    private String originalScheduleDays = "";
    private String originalDirectUsers = "";
    private String originalIndirectUsers = "";
    private String originalStatus = "";

    private final ChangeListener<Object> changeListener = (obs, oldVal, newVal) -> checkIfChanged();

    public void setParentController(GUI_CheckProjectRequestListController parentController) {
        this.parentController = parentController;
    }

    public void setProjectRequestDAO(ProjectRequestDAO projectRequestDAO) {
        this.projectRequestDAO = projectRequestDAO;
    }

    @FXML
    public void initialize() {
        statusCombo.setItems(FXCollections.observableArrayList("Pendiente", "Aprobado", "Rechazado"));
        if (projectRequestDAO == null) {
            projectRequestDAO = new ProjectRequestDAO();
        }
        setButtons();
    }

    private void setButtons() {
        if (saveButton != null) {
            saveButton.setOnAction(event -> handleSaveChanges());
            saveButton.setDisable(true);
        }
        if (cancelButton != null) {
            cancelButton.setOnAction(event -> handleCancel());
        }
    }

    public void setProjectRequestData(ProjectRequestDTO projectRequest) {
        if (projectRequest == null) {
            LOGGER.error("El objeto ProjectRequestDTO es nulo.");
            statusLabel.setText("Error: El objeto de solicitud de proyecto es nulo.");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        removeFieldListeners();

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
        statusCombo.setValue(mapStatusToCombo(projectRequest.getStatus()));

        setOriginalValues();

        addFieldListeners();

        checkIfChanged();
    }

    private void setOriginalValues() {
        originalTuition = fieldTuition.getText();
        originalOrganizationId = organizationIdField.getText();
        originalProjectId = projectIdField.getText();
        originalRepresentativeId = representativeIdField.getText();
        originalDescription = descriptionField.getText();
        originalGeneralObjective = generalObjectiveField.getText();
        originalDuration = durationField.getText();
        originalScheduleDays = scheduleDaysField.getText();
        originalDirectUsers = directUsersField.getText();
        originalIndirectUsers = indirectUsersField.getText();
        originalStatus = statusCombo.getValue();
    }

    private void addFieldListeners() {
        fieldTuition.textProperty().addListener(changeListener);
        organizationIdField.textProperty().addListener(changeListener);
        projectIdField.textProperty().addListener(changeListener);
        representativeIdField.textProperty().addListener(changeListener);
        descriptionField.textProperty().addListener(changeListener);
        generalObjectiveField.textProperty().addListener(changeListener);
        durationField.textProperty().addListener(changeListener);
        scheduleDaysField.textProperty().addListener(changeListener);
        directUsersField.textProperty().addListener(changeListener);
        indirectUsersField.textProperty().addListener(changeListener);
        statusCombo.valueProperty().addListener(changeListener);
    }

    private void removeFieldListeners() {
        fieldTuition.textProperty().removeListener(changeListener);
        organizationIdField.textProperty().removeListener(changeListener);
        projectIdField.textProperty().removeListener(changeListener);
        representativeIdField.textProperty().removeListener(changeListener);
        descriptionField.textProperty().removeListener(changeListener);
        generalObjectiveField.textProperty().removeListener(changeListener);
        durationField.textProperty().removeListener(changeListener);
        scheduleDaysField.textProperty().removeListener(changeListener);
        directUsersField.textProperty().removeListener(changeListener);
        indirectUsersField.textProperty().removeListener(changeListener);
        statusCombo.valueProperty().removeListener(changeListener);
    }

    private void checkIfChanged() {
        boolean changed =
                !fieldTuition.getText().equals(originalTuition) ||
                        !organizationIdField.getText().equals(originalOrganizationId) ||
                        !projectIdField.getText().equals(originalProjectId) ||
                        !representativeIdField.getText().equals(originalRepresentativeId) ||
                        !descriptionField.getText().equals(originalDescription) ||
                        !generalObjectiveField.getText().equals(originalGeneralObjective) ||
                        !durationField.getText().equals(originalDuration) ||
                        !scheduleDaysField.getText().equals(originalScheduleDays) ||
                        !directUsersField.getText().equals(originalDirectUsers) ||
                        !indirectUsersField.getText().equals(originalIndirectUsers) ||
                        (statusCombo.getValue() != null && !statusCombo.getValue().equals(originalStatus));

        boolean filled = areFieldsFilled();

        if (saveButton != null) {
            saveButton.setDisable(!(changed && filled));
        }
    }

    private ProjectStatus mapStatusFromCombo(String value) {
        switch (value) {
            case "Pendiente":
                return ProjectStatus.pending;
            case "Aprobado":
                return ProjectStatus.approved;
            case "Rechazado":
                return ProjectStatus.refused;
            default:
                return ProjectStatus.pending;
        }
    }

    private String mapStatusToCombo(ProjectStatus status) {
        if (status == null) return "Pendiente";
        switch (status) {
            case pending:
                return "Pendiente";
            case approved:
                return "Aprobado";
            case refused:
                return "Rechazado";
            default:
                return "Pendiente";
        }
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
            projectRequest.setStatus(mapStatusFromCombo(statusCombo.getValue()));

            boolean result = projectRequestDAO.updateProjectRequest(projectRequest);

            if (result) {
                statusLabel.setText("¡Solicitud de proyecto actualizada exitosamente!");
                statusLabel.setTextFill(Color.GREEN);

                setOriginalValues();
                if (saveButton != null) {
                    saveButton.setDisable(true);
                }
                if (parentController != null) {
                    parentController.loadRequestData();
                }
            } else {
                statusLabel.setText("No se pudo actualizar la solicitud de proyecto.");
                statusLabel.setTextFill(Color.RED);
            }

        }  catch (NumberFormatException e) {
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
                !descriptionField.getText().isEmpty() &&
                !generalObjectiveField.getText().isEmpty() &&
                !durationField.getText().isEmpty() &&
                !scheduleDaysField.getText().isEmpty() &&
                !directUsersField.getText().isEmpty() &&
                !indirectUsersField.getText().isEmpty() &&
                statusCombo.getValue() != null;
    }
}