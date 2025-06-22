package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import logic.DTO.DepartmentDTO;
import logic.DTO.LinkedOrganizationDTO;
import logic.DTO.RepresentativeDTO;
import logic.exceptions.InvalidData;
import logic.exceptions.RepeatedId;
import logic.services.LinkedOrganizationService;
import logic.services.RepresentativeService;
import logic.services.ServiceConfig;
import logic.exceptions.EmptyFields;
import logic.DAO.DepartmentDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GUI_RegisterRepresentativeController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_RegisterRepresentativeController.class);

    @FXML
    private Button registerUserButton;

    @FXML
    private TextField emailField;

    @FXML
    private Label label;

    @FXML
    private TextField nameField;

    @FXML
    private ChoiceBox<LinkedOrganizationDTO> organizationBox;

    @FXML
    private ChoiceBox<DepartmentDTO> departmentBox;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField surnameField;

    private GUI_CheckRepresentativeListController parentController;

    private LinkedOrganizationService linkedOrganizationService;
    private RepresentativeService representativeService;
    private DepartmentDAO departmentDAO = new DepartmentDAO();

    public void setParentController(GUI_CheckRepresentativeListController parentController) {
        this.parentController = parentController;
    }

    @FXML
    public void initialize() {
        try {
            ServiceConfig serviceConfig = new ServiceConfig();
            representativeService = serviceConfig.getRepresentativeService();
            linkedOrganizationService = serviceConfig.getLinkedOrganizationService();

            organizationBox.setItems(FXCollections.observableArrayList(getOrganizations()));
            organizationBox.setConverter(new StringConverter<LinkedOrganizationDTO>() {
                @Override
                public String toString(LinkedOrganizationDTO org) {
                    return org != null ? org.getName() : "";
                }
                @Override
                public LinkedOrganizationDTO fromString(String string) {
                    return null;
                }
            });

            organizationBox.setOnAction(event -> loadDepartmentsForSelectedOrganization());

            departmentBox.setConverter(new StringConverter<DepartmentDTO>() {
                @Override
                public String toString(DepartmentDTO dept) {
                    return dept != null ? dept.getName() : "";
                }
                @Override
                public DepartmentDTO fromString(String string) {
                    return null;
                }
            });

        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos desconocida.");
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if ("42S02".equals(sqlState)) {
                statusLabel.setText("Tabla no encontrada. Por favor, verifique la configuración.");
                LOGGER.error("Tabla no encontrada: {}", e.getMessage(), e);
            } else if ("42S22".equals(sqlState)) {
                statusLabel.setText("Columna no encontrada. Por favor, verifique la configuración.");
                LOGGER.error("Columna no encontrada: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error con la base de datos al inicializar los servicios.");
                LOGGER.error("Error al inicializar los servicios: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al inicializar la ventana.");
            LOGGER.error("Error inesperado al inicializar la ventana: {}", e.getMessage(), e);
        }
    }

    private void loadDepartmentsForSelectedOrganization() {
        departmentBox.getItems().clear();
        LinkedOrganizationDTO org = organizationBox.getValue();
        if (org == null) return;
        try {
            int orgId = Integer.parseInt(org.getIdOrganization());
            List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(orgId);
            departmentBox.setItems(FXCollections.observableArrayList(departments));
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if ("42S02".equals(sqlState)) {
                statusLabel.setText("Tabla no encontrada. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla no encontrada: {}", e.getMessage(), e);
            } else if ("42S22".equals(sqlState)) {
                statusLabel.setText("Columna no encontrada. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error de base de datos al cargar los departamentos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de base de datos al cargar los departamentos: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar los departamentos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al cargar los departamentos: {}", e.getMessage(), e);
        }
    }

    @FXML
    void handleRegisterRepresentative() {
        try {
            if (!areFieldsFilled()) {
                throw new EmptyFields("Todos los campos deben estar llenos.");
            }

            String names = nameField.getText();
            String surname = surnameField.getText();
            String email = emailField.getText();
            DepartmentDTO selectedDept = departmentBox.getValue();
            LinkedOrganizationDTO selectedOrg = organizationBox.getValue();

            if (selectedDept == null) {
                throw new InvalidData("Debe seleccionar un departamento válido.");
            }
            if (selectedOrg == null) {
                throw new InvalidData("Debe seleccionar una organización válida.");
            }

            String departmentId = String.valueOf(selectedDept.getDepartmentId());
            String organizationId = selectedOrg.getIdOrganization();

            RepresentativeDTO representative = new RepresentativeDTO(
                    "0", names, surname, email, organizationId, departmentId
            );

            try {
                boolean success = representativeService.registerRepresentative(representative);

                if (success) {
                    statusLabel.setText("¡Representante registrado exitosamente!");
                    statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);

                    organizationBox.setItems(FXCollections.observableArrayList(getOrganizations()));
                    departmentBox.getItems().clear();
                } else {
                    statusLabel.setText("El representante ya existe.");
                    statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                }
            } catch (SQLException e) {
                String sqlState = e.getSQLState();
                if ("08001".equals(sqlState)) {
                    statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                    LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                } else if ("08S01".equals(sqlState)) {
                    statusLabel.setText("Conexión interrumpida con la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                } else if ("28000".equals(sqlState)) {
                    statusLabel.setText("Acceso denegado a la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                } else if ("23505".equals(sqlState)) {
                    statusLabel.setText("El representante ya existe en la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("El representante ya existe: {}", e.getMessage(), e);
                } else if ("42000".equals(sqlState)) {
                    statusLabel.setText("Base de datos desconocida.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                } else if ("42S02".equals(sqlState)) {
                    statusLabel.setText("Tabla no encontrada.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Tabla no encontrada: {}", e.getMessage(), e);
                } else if ("42S22".equals(sqlState)) {
                    statusLabel.setText("Columna no encontrada.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Columna no encontrada: {}", e.getMessage(), e);
                } else {
                    statusLabel.setText("Error de base de datos al registrar el representante.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Error de base de datos al registrar el representante: {}", e.getMessage(), e);
                }
            } catch (Exception e) {
                statusLabel.setText("Error inesperado al registrar el representante.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error inesperado al registrar el representante: {}", e.getMessage(), e);
            }

            if (parentController != null) {
                parentController.loadOrganizationData();
            }
        } catch (EmptyFields | InvalidData | RepeatedId e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al registrar representante: {}", e.getMessage(), e);
        }
    }

    public List<LinkedOrganizationDTO> getOrganizations() {
        try {
            return linkedOrganizationService.getAllLinkedOrganizations();
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                return List.of();
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos desconocida.");
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                return List.of();
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                return List.of();
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                return List.of();
            } else if ("42S02".equals(sqlState)) {
                statusLabel.setText("Tabla no encontrada. Por favor, verifique la configuración.");
                LOGGER.error("Tabla no encontrada: {}", e.getMessage(), e);
                return List.of();
            } else if ("42S22".equals(sqlState)) {
                statusLabel.setText("Columna no encontrada. Por favor, verifique la configuración.");
                LOGGER.error("Columna no encontrada: {}", e.getMessage(), e);
                return List.of();
            } else {
                statusLabel.setText("Error al cargar las organizaciones.");
                LOGGER.error("Error al cargar las organizaciones: {}", e.getMessage(), e);
                return List.of();
            }
        } catch (IOException e) {
            statusLabel.setText("Error de entrada/salida al cargar las organizaciones.");
            LOGGER.error("Error de entrada/salida al cargar las organizaciones: {}", e.getMessage(), e);
            return List.of();
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar las organizaciones.");
            LOGGER.error("Error inesperado al cargar las organizaciones: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public boolean areFieldsFilled() {
        return !nameField.getText().isEmpty() &&
                !surnameField.getText().isEmpty() &&
                !emailField.getText().isEmpty() &&
                organizationBox.getValue() != null &&
                departmentBox.getValue() != null;
    }
}