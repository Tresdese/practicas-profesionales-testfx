package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DTO.LinkedOrganizationDTO;
import logic.DTO.ProjectDTO;
import logic.DTO.Role;
import logic.DTO.UserDTO;
import logic.exceptions.EmptyFields;
import logic.exceptions.InvalidData;
import logic.exceptions.RepeatedId;
import logic.services.LinkedOrganizationService;
import logic.services.ProjectService;
import logic.services.ServiceConfig;
import logic.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.scene.paint.Color;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public class GUI_RegisterProjectController {

    private static final Logger logger = LogManager.getLogger(GUI_RegisterProjectController.class);

//    @FXML
//    private Button buttonRegisterProyect;

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ChoiceBox<String> academicBox;

    @FXML
    private ChoiceBox<String> organizationBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Label statusLabel;

    @FXML
    private Label label;

    private LinkedOrganizationService linkedOrganizationService;
    private UserService userService;
    private ProjectService projectService;

    @FXML
    public void initialize() {
        try {
            ServiceConfig serviceConfig = new ServiceConfig();
            linkedOrganizationService = serviceConfig.getLinkedOrganizationService();
            projectService = serviceConfig.getProjectService();
            userService = serviceConfig.getUserService();

            for (String organizationName : getOrganizationNames()) {
                organizationBox.getItems().add(organizationName);
            }

            for (String academicName : getAcademicNames()) {
                academicBox.getItems().add(academicName);
            }
        } catch (SQLException e) {
            statusLabel.setText("Error al conectar a la base de datos.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error al inicializar los servicios: {}", e.getMessage(), e);
        }
    }

    @FXML
    void handleRegisterProject(ActionEvent event) {
        try {
            if (!areFieldsFilled()) {
                throw new EmptyFields("Todos los campos deben estar llenos.");
            }

            String name = nameField.getText();
            String description = descriptionField.getText();
            String academic = academicBox.getValue();
            String organization = organizationBox.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            LinkedOrganizationDTO linkedOrganization = linkedOrganizationService.searchLinkedOrganizationByName(organization);
            if (linkedOrganization == null || linkedOrganization.getIdOrganization() == null) {
                throw new InvalidData("La organización seleccionada no es válida.");
            }

            String organizationId = linkedOrganization.getIdOrganization();
            String academicId = getAcademicIdByName(academic);

            ProjectDTO project = new ProjectDTO(
                    "0",
                    name,
                    description,
                    endDate != null ? Timestamp.valueOf(endDate.atStartOfDay()) : null,
                    startDate != null ? Timestamp.valueOf(startDate.atStartOfDay()) : null,
                    academicId,
                    Integer.parseInt(organizationId)
            );

            boolean success = projectService.registerProject(project);

            if (success) {
                clearFields();
                statusLabel.setText("¡Proyecto registrado exitosamente!");
                statusLabel.setTextFill(Color.GREEN);

                organizationBox.getItems().clear();
                academicBox.getItems().clear();

                for (String organizationName : getOrganizationNames()) {
                    organizationBox.getItems().add(organizationName);
                }

                for (String academicName : getAcademicNames()) {
                    academicBox.getItems().add(academicName);
                }
            } else {
                statusLabel.setText("El proyecto ya existe.");
                statusLabel.setTextFill(Color.RED);
            }
        } catch (EmptyFields | InvalidData | RepeatedId e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(Color.RED);
            logger.error("Error: {}", e.getMessage(), e);
        } catch (SQLException e) {
            statusLabel.setText("No se pudo conectar a la base de datos. Por favor, intente más tarde.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error de SQL al registrar el proyecto: {}", e.getMessage(), e);
        }
    }

    private void clearFields() {
        nameField.clear();
        descriptionField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }

    public List<String> getOrganizationNames() {
        ObservableList<LinkedOrganizationDTO> organizationList = FXCollections.observableArrayList();
        try {
            List<LinkedOrganizationDTO> organizations = linkedOrganizationService.getAllLinkedOrganizations();
            organizationList.addAll(organizations);
        } catch (SQLException e) {
            statusLabel.setText("Error al cargar los datos de las organizaciones.");
            logger.error("Error al cargar los datos de las organizaciones: {}", e.getMessage(), e);
        }
        return organizationList.stream()
                .map(LinkedOrganizationDTO::getName)
                .toList();
    }

    public List<String> getAcademicNames() {
        ObservableList<UserDTO> academicList = FXCollections.observableArrayList();
        try {
            List<UserDTO> academics = userService.getAllUsers();
            academics = academics.stream()
                    .filter(user -> user.getRole() == Role.ACADEMICO)
                    .toList();
            academicList.addAll(academics);
        } catch (SQLException e) {
            statusLabel.setText("Error al cargar los académicos.");
            logger.error("Error al cargar los académicos: {}", e.getMessage(), e);
        }
        return academicList.stream()
                .map(UserDTO::getNames)
                .toList();
    }

    private String getAcademicIdByName(String name) {
        try {
            List<UserDTO> academics = userService.getAllUsers();
            for (UserDTO academic : academics) {
                if (academic.getNames().equals(name)) {
                    return academic.getIdUser();
                }
            }
            throw new InvalidData("No se encontró el académico seleccionado");
        } catch (SQLException e) {
            logger.error("Error al buscar el ID del académico: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar el académico: " + e.getMessage());
        }
    }

    public boolean areFieldsFilled() {
        return !nameField.getText().isEmpty() &&
                !descriptionField.getText().isEmpty() &&
                academicBox.getValue() != null &&
                organizationBox.getValue() != null &&
                startDatePicker.getValue() != null &&
                endDatePicker.getValue() != null;
    }
}