package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import logic.DAO.DepartmentDAO;
import logic.DTO.*;
import logic.exceptions.EmptyFields;
import logic.exceptions.InvalidData;
import logic.exceptions.RepeatedId;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public class GUI_RegisterProjectController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_RegisterProjectController.class);

    private static final int MAX_NAME = 100;
    private static final int MAX_DESCRIPTION = 300;

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ChoiceBox<UserDTO> academicChoiceBox;

    @FXML
    private ChoiceBox<LinkedOrganizationDTO> organizationChoiceBox;

    @FXML
    private ChoiceBox<DepartmentDTO> departmentChoiceBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Button registerProjectButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label nameCharCountLabel;

    @FXML
    private Label descriptionCharCountLabel;

    private ServiceConfig serviceConfig;
    private ProjectService projectService;
    private LinkedOrganizationService organizationService;
    private UserService userService;
    private DepartmentDAO departmentDAO;

    private ObservableList<UserDTO> academicList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            if (serviceConfig == null) serviceConfig = new ServiceConfig();
            if (projectService == null) projectService = serviceConfig.getProjectService();
            if (organizationService == null) organizationService = serviceConfig.getLinkedOrganizationService();
            if (userService == null) userService = serviceConfig.getUserService();
            if (departmentDAO == null) departmentDAO = new DepartmentDAO();

            loadAcademics();
            loadOrganizations();
            loadDepartments();

            registerProjectButton.setOnAction(event -> handleRegisterProject());

            nameField.setTextFormatter(createTextFormatter(MAX_NAME));
            descriptionField.setTextFormatter(createTextFormatter(MAX_DESCRIPTION));
            configureCharCount(nameField, nameCharCountLabel, MAX_NAME);
            configureCharCount(descriptionField, descriptionCharCountLabel, MAX_DESCRIPTION);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
                @Override
                public String toString(LocalDate date) {
                    return date != null ? dateFormatter.format(date) : "";
                }
                @Override
                public LocalDate fromString(String string) {
                    if (string == null || string.trim().isEmpty()) {
                        return LocalDate.now();
                    }
                    try {
                        return LocalDate.parse(string, dateFormatter);
                    } catch (DateTimeParseException e) {
                        return LocalDate.now();
                    }
                }
            };
            startDatePicker.setConverter(converter);
            endDatePicker.setConverter(converter);
            startDatePicker.getEditor().setDisable(true);
            endDatePicker.getEditor().setDisable(true);

        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)){
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos no encontrada: {}", e.getMessage(), e);
            } else if ("40S02".equals(sqlState)) {
                statusLabel.setText("Tabla de académicos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de académicos no encontrada: {}", e);
            } else if ("40S22".equals(sqlState)) {
                statusLabel.setText("Columna de académicos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna de académicos no encontrada: {}", e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al inicializar el servicio.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al inicializar el servicio: {}", e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al leer el archivo de configuración.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al leer el archivo de configuración: {}", e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al inicializar la vista.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al inicializar la vista: {}", e);
        }
    }

    private TextFormatter<String> createTextFormatter(int maxLength) {
        return new TextFormatter<>(change ->
                change.getControlNewText().length() <= maxLength ? change : null
        );
    }

    private void configureCharCount(TextInputControl textField, Label charCountLabel, int maxLength) {
        if (charCountLabel == null) return;
        charCountLabel.setText("0/" + maxLength);
        textField.textProperty().addListener((obs, oldText, newText) ->
                charCountLabel.setText(newText.length() + "/" + maxLength)
        );
    }

    public void loadAcademics() {
        try {
            List<UserDTO> academics;
            if (userService != null) {
                academics = userService.getAllUsers().stream()
                        .filter(user -> user.getRole() == Role.ACADEMICO)
                        .collect(Collectors.toList());
            } else {
                academics = FXCollections.observableArrayList();
            }
            academicList.setAll(academics);
            academicChoiceBox.setItems(academicList);
            academicChoiceBox.setConverter(new StringConverter<UserDTO>() {
                @Override
                public String toString(UserDTO user) {
                    return user == null ? "" : user.getNames() + " " + user.getSurnames();
                }
                @Override
                public UserDTO fromString(String string) {
                    for (UserDTO user : academicChoiceBox.getItems()) {
                        String fullName = user.getNames() + " " + user.getSurnames();
                        if (fullName.equals(string)) {
                            return user;
                        }
                    }
                    return new UserDTO();
                }
            });
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, verifica tu conexión.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos. Por favor, verifica tu conexión.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos no encontrada: {}", e);
            } else if ("40S02".equals(sqlState)) {
                statusLabel.setText("Tabla de académicos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de académicos no encontrada: {}", e);
            } else if ("40S22".equals(sqlState)) {
                statusLabel.setText("Columna de académicos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna de académicos no encontrada: {}", e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e);
            } else {
                statusLabel.setText("Error al cargar académicos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al cargar académicos: {}", e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar académicos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al cargar académicos: {}", e);
        }
    }

    public void loadOrganizations() {
        try {
            List<LinkedOrganizationDTO> organizations = organizationService.getAllLinkedOrganizations();
            organizationChoiceBox.setItems(FXCollections.observableArrayList(organizations));
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, verifica tu conexión.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos. Por favor, verifica tu conexión.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos no encontrada: {}", e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e);
            } else {
                statusLabel.setText("Error al cargar organizaciones.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al cargar organizaciones: {}", e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar organizaciones.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al cargar organizaciones: {}", e);
        }
    }

    public void loadDepartments() {
        try {
            List<DepartmentDTO> departments = departmentDAO.getAllDepartments();
            departmentChoiceBox.setItems(FXCollections.observableArrayList(departments));
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, verifica tu conexión.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos. Por favor, verifica tu conexión.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos no encontrada: {}", e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e);
            } else {
                statusLabel.setText("Error al cargar departamentos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al cargar departamentos: {}", e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar departamentos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al cargar departamentos: {}", e);
        }
    }

    @FXML
    private void handleRegisterProject() {
        try {
            if (!validateFields()) {
                throw new EmptyFields("Todos los campos son obligatorios.");
            }

            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            UserDTO academic = academicChoiceBox.getValue();
            LinkedOrganizationDTO organization = organizationChoiceBox.getValue();
            DepartmentDTO department = departmentChoiceBox.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            ProjectDTO project = new ProjectDTO(
                    null,
                    name,
                    description,
                    endDate != null ? Timestamp.valueOf(endDate.atStartOfDay()) : null,
                    startDate != null ? Timestamp.valueOf(startDate.atStartOfDay()) : null,
                    academic != null ? academic.getIdUser() : null,
                    organization != null ? Integer.parseInt(organization.getIdOrganization()) : 0,
                    department != null ? department.getDepartmentId() : 0
            );

            boolean success = projectService.registerProject(project);

            if (success) {
                statusLabel.setText("Proyecto registrado correctamente.");
                statusLabel.setTextFill(Color.GREEN);
                clearFields();
            } else {
                statusLabel.setText("No se pudo registrar el proyecto.");
                statusLabel.setTextFill(Color.RED);
            }
        } catch (EmptyFields | InvalidData | RepeatedId e) {
            LOGGER.error("Error de validación al registrar proyecto: {}", e);
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(Color.RED);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                LOGGER.error("Error de conexión con la base de datos: {}", e);
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
            } else if ("08S01".equals(sqlState)) {
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e);
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
            } else if ("42000".equals(sqlState)) {
                LOGGER.error("Base de datos no encontrada: {}", e);
                statusLabel.setText("Base de datos no encontrada.");
                statusLabel.setTextFill(Color.RED);
            } else if ("28000".equals(sqlState)) {
                LOGGER.error("Acceso denegado a la base de datos: {}", e);
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
            } else if ("23000".equals(sqlState)) {
                LOGGER.error("Violación de restricción de integridad: {}", e);
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
            }
             else {
                LOGGER.error("Error al registrar proyecto: {}", e);
                statusLabel.setText("Error al registrar el proyecto.");
                statusLabel.setTextFill(Color.RED);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado al registrar proyecto: {}", e);
            statusLabel.setText("Error inesperado.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    private boolean validateFields() {
        return !nameField.getText().trim().isEmpty()
                && !descriptionField.getText().trim().isEmpty()
                && academicChoiceBox.getValue() != null
                && organizationChoiceBox.getValue() != null
                && departmentChoiceBox.getValue() != null
                && startDatePicker.getValue() != null
                && endDatePicker.getValue() != null;
    }

    private void clearFields() {
        nameField.clear();
        descriptionField.clear();
        academicChoiceBox.getSelectionModel().clearSelection();
        organizationChoiceBox.getSelectionModel().clearSelection();
        departmentChoiceBox.getSelectionModel().clearSelection();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }
}