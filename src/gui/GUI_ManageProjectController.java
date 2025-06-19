package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import logic.DTO.DepartmentDTO;
import logic.DTO.LinkedOrganizationDTO;
import logic.DTO.ProjectDTO;
import logic.DTO.UserDTO;
import logic.DAO.DepartmentDAO;
import logic.services.LinkedOrganizationService;
import logic.services.ProjectService;
import logic.services.ServiceConfig;
import logic.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public class GUI_ManageProjectController {

    private static final Logger logger = LogManager.getLogger(GUI_ManageProjectController.class);

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private DatePicker startDatePicker, endDatePicker;

    @FXML
    private ChoiceBox<String> organizationBox, academicBox, statusBox;

    @FXML
    private ChoiceBox<DepartmentDTO> departmentBox;

    @FXML
    private Label statusLabel;

    private GUI_CheckProjectListController parentController;

    private ProjectDTO project;
    private ProjectService projectService;
    private LinkedOrganizationService linkedOrganizationService;
    private UserService userService;
    private DepartmentDAO departmentDAO = new DepartmentDAO();

    public void setParentController(GUI_CheckProjectListController parentController) {
        this.parentController = parentController;
    }

    @FXML
    public void initialize() {
        try {
            ServiceConfig serviceConfig = new ServiceConfig();
            projectService = serviceConfig.getProjectService();
            linkedOrganizationService = serviceConfig.getLinkedOrganizationService();
            userService = serviceConfig.getUserService();

            loadOrganizations();
            loadAcademics();
            loadStatusOptions();

            departmentBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(DepartmentDTO dept) {
                    return dept == null ? "" : dept.getName();
                }
                @Override
                public DepartmentDTO fromString(String s) {
                    return null;
                }
            });

            organizationBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                loadDepartmentsForSelectedOrganization();
                departmentBox.setValue(null);
            });

        } catch (SQLException e) {
            logger.error("Error al inicializar los servicios: {}", e.getMessage(), e);
        }
    }

    private void loadOrganizations() {
        try {
            List<LinkedOrganizationDTO> organizations = linkedOrganizationService.getAllLinkedOrganizations();
            List<String> organizationNames = organizations.stream()
                    .map(LinkedOrganizationDTO::getName)
                    .toList();

            organizationBox.getItems().clear();
            organizationBox.getItems().addAll(organizationNames);
        } catch (SQLException e) {
            statusLabel.setText("Error al cargar las organizaciones.");
            logger.error("Error al cargar las organizaciones: {}", e.getMessage(), e);
        }
    }

    private void loadDepartmentsForSelectedOrganization() {
        departmentBox.getItems().clear();
        String orgName = organizationBox.getValue();
        if (orgName == null) return;
        try {
            LinkedOrganizationDTO org = linkedOrganizationService.searchLinkedOrganizationByName(orgName);
            if (org != null && org.getIdOrganization() != null) {
                int orgId = Integer.parseInt(org.getIdOrganization());
                List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(orgId);
                departmentBox.setItems(FXCollections.observableArrayList(departments));
            }
        } catch (Exception e) {
            logger.error("Error al cargar departamentos: {}", e.getMessage(), e);
        }
    }

    private void loadAcademics() {
        try {
            List<UserDTO> academics = userService.getAllUsers();
            List<String> academicNames = academics.stream()
                    .map(academic -> academic.getNames() + " " + academic.getSurnames())
                    .toList();

            academicBox.getItems().clear();
            academicBox.getItems().addAll(academicNames);
        } catch (SQLException e) {
            statusLabel.setText("Error al cargar los académicos.");
            logger.error("Error al cargar los académicos: {}", e.getMessage(), e);
        }
    }

    private void loadStatusOptions() {
        statusBox.getItems().clear();
        statusBox.getItems().addAll("Pendiente", "En curso", "Finalizado", "Cancelado");
    }

    public void setProjectData(ProjectDTO project) {
        if (project == null) {
            logger.error("El objeto ProjectDTO es nulo.");
            return;
        }

        this.project = project;

        nameField.setText(project.getName() != null ? project.getName() : "");
        descriptionArea.setText(project.getDescription() != null ? project.getDescription() : "");

        if (project.getStartDate() != null) {
            LocalDate startDate = project.getStartDate().toLocalDateTime().toLocalDate();
            startDatePicker.setValue(startDate);
        }

        if (project.getApproximateDate() != null) {
            LocalDate endDate = project.getApproximateDate().toLocalDateTime().toLocalDate();
            endDatePicker.setValue(endDate);
        }

        try {
            int orgId = project.getIdOrganization();
            if (orgId > 0) {
                LinkedOrganizationDTO organization = linkedOrganizationService.searchLinkedOrganizationById(String.valueOf(orgId));
                if (organization != null) {
                    organizationBox.setValue(organization.getName());
                    loadDepartmentsForSelectedOrganization();
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener la organización del proyecto: {}", e.getMessage(), e);
        }

        // Selecciona el departamento correspondiente
        try {
            int deptId = project.getIdDepartment();
            if (deptId > 0) {
                DepartmentDTO department = departmentDAO.searchDepartmentById(deptId);
                if (department != null) {
                    departmentBox.setValue(department);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener el departamento del proyecto: {}", e.getMessage(), e);
        }

        try {
            String userId = project.getIdUser();
            if (userId != null && !userId.isEmpty()) {
                UserDTO academic = userService.searchUserById(userId);
                if (academic != null) {
                    academicBox.setValue(academic.getNames() + " " + academic.getSurnames());
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener el académico del proyecto: {}", e.getMessage(), e);
        }

        statusBox.setValue("En curso");
    }

    @FXML
    private void handleSaveChanges() {
        try {
            if (!areFieldsFilled()) {
                throw new IllegalArgumentException("Todos los campos obligatorios deben estar llenos.");
            }

            String name = nameField.getText();
            String description = descriptionArea.getText();
            String organizationName = organizationBox.getValue();
            String academicFullName = academicBox.getValue();

            LocalDate startLocalDate = startDatePicker.getValue();
            LocalDate approximateLocalDate = endDatePicker.getValue();

            if (startLocalDate == null) {
                throw new IllegalArgumentException("La fecha de inicio debe ser seleccionada.");
            }

            Timestamp startDate = Timestamp.valueOf(startLocalDate.atStartOfDay());
            Timestamp approximateDate = approximateLocalDate != null ?
                    Timestamp.valueOf(approximateLocalDate.atStartOfDay()) : null;

            project.setName(name);
            project.setDescription(description);
            project.setStartDate(startDate);
            project.setApproximateDate(approximateDate);

            if (organizationName != null && !organizationName.isEmpty()) {
                LinkedOrganizationDTO linkedOrganization = linkedOrganizationService.searchLinkedOrganizationByName(organizationName);
                if (linkedOrganization == null || linkedOrganization.getIdOrganization() == null) {
                    throw new IllegalArgumentException("La organización seleccionada no es válida.");
                }
                project.setIdOrganization(Integer.parseInt(linkedOrganization.getIdOrganization()));
            }

            if (academicFullName != null && !academicFullName.isEmpty()) {
                List<UserDTO> users = userService.getAllUsers();
                for (UserDTO user : users) {
                    String fullName = user.getNames() + " " + user.getSurnames();
                    if (fullName.equals(academicFullName)) {
                        project.setIdUser(user.getIdUser());
                        break;
                    }
                }
            }

            DepartmentDTO selectedDepartment = departmentBox.getValue();
            if (selectedDepartment != null) {
                project.setIdDepartment(selectedDepartment.getDepartmentId());
            } else {
                throw new IllegalArgumentException("Debe seleccionar un departamento.");
            }

            boolean success = projectService.updateProject(project);

            if (success) {
                statusLabel.setText("¡Proyecto actualizado exitosamente!");
                statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);

                if (parentController != null) {
                    parentController.loadProjectData();
                }
            } else {
                statusLabel.setText("No se pudo actualizar el proyecto.");
                statusLabel.setTextFill(Color.RED);
            }
        } catch (IllegalArgumentException e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(Color.RED);
            logger.error("Error de validación: {}", e.getMessage(), e);
        } catch (SQLException e) {
            statusLabel.setText("Error de base de datos al actualizar el proyecto.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error de SQL: {}", e.getMessage(), e);
        } catch (NullPointerException e) {
            statusLabel.setText("Error: Datos requeridos no disponibles.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error por referencia nula: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al actualizar el proyecto.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error inesperado: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }

    private boolean areFieldsFilled() {
        return !nameField.getText().isEmpty() &&
                !descriptionArea.getText().isEmpty() &&
                startDatePicker.getValue() != null &&
                organizationBox.getValue() != null &&
                academicBox.getValue() != null &&
                departmentBox.getValue() != null;
    }
}