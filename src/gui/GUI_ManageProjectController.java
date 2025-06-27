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

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public class GUI_ManageProjectController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_ManageProjectController.class);

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private DatePicker startDatePicker, endDatePicker;

    @FXML
    private ChoiceBox<String> organizationChoiceBox, academicChoiceBox, statusChoiceBox;

    @FXML
    private ChoiceBox<DepartmentDTO> departmentChoiceBox;

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

            departmentChoiceBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(DepartmentDTO dept) {
                    return dept == null ? "" : dept.getName();
                }
                @Override
                public DepartmentDTO fromString(String s) {
                    throw new UnsupportedOperationException("Conversión desde String a DepartmentDTO no soportada.");
                }
            });

            organizationChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                loadDepartmentsForSelectedOrganization();
                departmentChoiceBox.setValue(null);
            });

        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexion con la base de datos: {} ", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("40S02")) {
                statusLabel.setText("Tabla de organizaciones no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de organizaciones no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("40S22")) {
                statusLabel.setText("Columna desconocida en la tabla de organizaciones.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna desconocida en la tabla de organizaciones: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            }
             else {
                statusLabel.setText("Error al inicializar los servicios.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al inicializar los servicios: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al leer el archivo de configuracion de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error al inicializar los servicios.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al inicializar los servicios: {} ", e.getMessage(), e);
        }
    }

    private void loadOrganizations() {
        try {
            List<LinkedOrganizationDTO> organizations = linkedOrganizationService.getAllLinkedOrganizations();
            List<String> organizationNames = organizations.stream()
                    .map(LinkedOrganizationDTO::getName)
                    .toList();

            organizationChoiceBox.getItems().clear();
            organizationChoiceBox.getItems().addAll(organizationNames);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar organizaciones.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al cargar organizaciones: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al leer el archivo de configuracion de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error al cargar organizaciones.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al cargar organizaciones: {}", e.getMessage(), e);
        }
    }

    private void loadDepartmentsForSelectedOrganization() {
        departmentChoiceBox.getItems().clear();
        String orgName = organizationChoiceBox.getValue();
        if (orgName == null) return;
        try {
            LinkedOrganizationDTO org = linkedOrganizationService.searchLinkedOrganizationByName(orgName);
            if (org != null && org.getIdOrganization() != null) {
                int orgId = Integer.parseInt(org.getIdOrganization());
                List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(orgId);
                departmentChoiceBox.setItems(FXCollections.observableArrayList(departments));
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("40S02")) {
                statusLabel.setText("Tabla de departamentos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de departamentos no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("40S22")) {
                statusLabel.setText("Columna desconocida en la tabla de departamentos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna desconocida en la tabla de departamentos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar departamentos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al cargar departamentos: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al leer el archivo de configuracion de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar departamentos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al cargar departamentos: {}", e.getMessage(), e);
        }
    }

    private void loadAcademics() {
        if (userService == null) {
            statusLabel.setText("No se pudo cargar la información del académico porque el servicio de usuarios no está disponible. Verifique la conexión a la base de datos o reinicie la aplicación.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("El servicio de usuarios no está disponible (userService es null).");
            return;
        }
        try {
            List<UserDTO> academics = userService.getAllUsers();
            List<String> academicNames = academics.stream()
                    .map(academic -> academic.getNames() + " " + academic.getSurnames())
                    .toList();

            academicChoiceBox.getItems().clear();
            academicChoiceBox.getItems().addAll(academicNames);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("40S02")) {
                statusLabel.setText("Tabla de usuarios no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de usuarios no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("40S22")) {
                statusLabel.setText("Columna desconocida en la tabla de usuarios.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna desconocida en la tabla de usuarios: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar académicos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al cargar académicos: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al leer el archivo de configuracion de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error al cargar académicos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al cargar académicos: {}", e.getMessage(), e);
        }
    }

    private void loadStatusOptions() {
        statusChoiceBox.getItems().clear();
        statusChoiceBox.getItems().addAll("Pendiente", "En curso", "Finalizado", "Cancelado");
    }

    public void setProjectData(ProjectDTO project) {
        if (project == null) {
            LOGGER.error("El objeto ProjectDTO es nulo.");
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

        if (userService == null) {
            statusLabel.setText("No se pudo cargar la información del académico porque el servicio de usuarios no está disponible. Verifique la conexión a la base de datos o reinicie la aplicación.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("El servicio de usuarios no está disponible (userService es null).");
            return;
        }

        try {
            int orgId = project.getIdOrganization();
            if (orgId > 0) {
                LinkedOrganizationDTO organization = linkedOrganizationService.searchLinkedOrganizationById(String.valueOf(orgId));
                if (organization != null) {
                    organizationChoiceBox.setValue(organization.getName());
                    loadDepartmentsForSelectedOrganization();
                }
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("40S02")) {
                statusLabel.setText("Tabla de organizaciones no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de organizaciones no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("40S22")) {
                statusLabel.setText("Columna desconocida en la tabla de organizaciones.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna desconocida en la tabla de organizaciones: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al obtener la organización del proyecto.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al obtener la organización del proyecto: {}", e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al leer el archivo de configuracion de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al obtener la organización del proyecto.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al obtener la organización del proyecto: {}", e);
        }

        try {
            int deptId = project.getIdDepartment();
            if (deptId > 0) {
                DepartmentDTO department = departmentDAO.searchDepartmentById(deptId);
                if (department != null) {
                    departmentChoiceBox.setValue(department);
                }
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("40S02")) {
                statusLabel.setText("Tabla de departamentos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de departamentos no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("40S22")) {
                statusLabel.setText("Columna desconocida en la tabla de departamentos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna desconocida en la tabla de departamentos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al obtener el departamento del proyecto.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al obtener el departamento del proyecto: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al leer el archivo de configuracion de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al obtener el departamento del proyecto.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al obtener el departamento del proyecto: {}", e.getMessage(), e);
        }

        try {
            String userId = project.getIdUser();
            if (userId != null && !userId.isEmpty()) {
                UserDTO academic = userService.searchUserById(userId);
                if (academic != null) {
                    academicChoiceBox.setValue(academic.getNames() + " " + academic.getSurnames());
                }
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("40S02")) {
                statusLabel.setText("Tabla de usuarios no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de usuarios no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("40S22")) {
                statusLabel.setText("Columna desconocida en la tabla de usuarios.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna desconocida en la tabla de usuarios: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al obtener el académico del proyecto.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al obtener el académico del proyecto: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al leer el archivo de configuracion de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al obtener el académico del proyecto.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al obtener el académico del proyecto: {}", e.getMessage(), e);
        }

        statusChoiceBox.setValue("En curso");
    }

    @FXML
    private void handleSaveChanges() {
        try {
            if (!areFieldsFilled()) {
                throw new IllegalArgumentException("Todos los campos obligatorios deben estar llenos.");
            }

            String name = nameField.getText();
            String description = descriptionArea.getText();
            String organizationName = organizationChoiceBox.getValue();
            String academicFullName = academicChoiceBox.getValue();

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

            DepartmentDTO selectedDepartment = departmentChoiceBox.getValue();
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
            LOGGER.error("Error de validación: {}", e.getMessage(), e);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexion con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("40S02")) {
                statusLabel.setText("Tabla de organizaciones no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de organizaciones no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("40S22")) {
                statusLabel.setText("Columna desconocida en la tabla de organizaciones.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna desconocida en la tabla de organizaciones: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("23000")) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al actualizar el proyecto.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al actualizar el proyecto: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al leer el archivo de configuracion de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
        } catch (NullPointerException e) {
            statusLabel.setText("Error: Datos requeridos no disponibles.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error por referencia nula: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al actualizar el proyecto.");
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
        return !nameField.getText().isEmpty() &&
                !descriptionArea.getText().isEmpty() &&
                startDatePicker.getValue() != null &&
                organizationChoiceBox.getValue() != null &&
                academicChoiceBox.getValue() != null &&
                departmentChoiceBox.getValue() != null;
    }
}