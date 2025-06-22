package gui;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import logic.DAO.DepartmentDAO;
import logic.DTO.DepartmentDTO;
import logic.DTO.LinkedOrganizationDTO;
import logic.DTO.RepresentativeDTO;
import logic.services.LinkedOrganizationService;
import logic.services.RepresentativeService;
import logic.services.ServiceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class GUI_ManageRepresentativeController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_ManageRepresentativeController.class);

    @FXML
    private TextField namesField, surnamesField, emailField;

    @FXML
    private ChoiceBox<String> organizationBox;

    @FXML
    private ChoiceBox<String> departmentBox;

    @FXML
    private Label statusLabel;

    private GUI_CheckRepresentativeListController parentController;

    private RepresentativeDTO representative;
    private RepresentativeService representativeService;
    private LinkedOrganizationService linkedOrganizationService;
    private DepartmentDAO departmentDAO = new DepartmentDAO();

    public void setParentController(GUI_CheckRepresentativeListController parentController) {
        this.parentController = parentController;
    }

    public void setRepresentativeService(RepresentativeService representativeService) {
        this.representativeService = representativeService;
    }

    @FXML
    public void initialize() {
        try {
            ServiceConfig serviceConfig = new ServiceConfig();
            representativeService = serviceConfig.getRepresentativeService();
            linkedOrganizationService = serviceConfig.getLinkedOrganizationService();

            loadOrganizations();

            organizationBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                loadDepartmentsForSelectedOrganization();
            });
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
            }
            else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            }
             else {
                statusLabel.setText("Error en la base de datos al inicializar el servicio.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error en la base de datos al inicializar el servicio: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al inicializar el servicio.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al inicializar el servicio: {}", e.getMessage(), e);
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
                LOGGER.error("Error al cargar organizaciones: {}", e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error al cargar organizaciones.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al cargar organizaciones: {}", e);
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
                List<String> departmentNames = departments.stream()
                        .map(DepartmentDTO::getName)
                        .toList();
                departmentBox.getItems().setAll(departmentNames);
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
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar departamentos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al cargar departamentos: {}", e.getMessage(), e);
            }
        }
        catch (Exception e) {
            statusLabel.setText("Error al cargar departamentos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al cargar departamentos: {}", e.getMessage(), e);
        }
    }

    public void setRepresentativeData(RepresentativeDTO representative) {
        if (representative == null) {
            LOGGER.error("El objeto RepresentativeDTO es nulo.");
            return;
        }

        this.representative = representative;

        namesField.setText(representative.getNames() != null ? representative.getNames() : "");
        surnamesField.setText(representative.getSurnames() != null ? representative.getSurnames() : "");
        emailField.setText(representative.getEmail() != null ? representative.getEmail() : "");

        try {
            String deptId = representative.getIdDepartment();
            if (deptId != null && !deptId.isEmpty()) {
                DepartmentDTO department = departmentDAO.searchDepartmentById(Integer.parseInt(deptId));
                if (department != null) {
                    LinkedOrganizationDTO org = linkedOrganizationService.searchLinkedOrganizationById(String.valueOf(department.getOrganizationId()));
                    if (org != null) {
                        organizationBox.setValue(org.getName());
                        loadDepartmentsForSelectedOrganization();
                        departmentBox.setValue(department.getName());
                    }
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
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar los datos del representante.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al cargar los datos del representante: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar los datos del representante.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al cargar los datos del representante: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleSaveChanges() {
        try {
            if (!areFieldsFilled()) {
                throw new IllegalArgumentException("Todos los campos deben estar llenos.");
            }

            String name = namesField.getText();
            String surname = surnamesField.getText();
            String email = emailField.getText();
            String departmentName = departmentBox.getValue();

            representative.setNames(name);
            representative.setSurnames(surname);
            representative.setEmail(email);

            String orgName = organizationBox.getValue();
            LinkedOrganizationDTO org = linkedOrganizationService.searchLinkedOrganizationByName(orgName);
            if (org == null) {
                throw new IllegalArgumentException("La organización seleccionada no es válida.");
            }
            int orgId = Integer.parseInt(org.getIdOrganization());
            DepartmentDTO department = departmentDAO.getAllDepartmentsByOrganizationId(orgId).stream()
                    .filter(d -> d.getName().equals(departmentName))
                    .findFirst()
                    .orElse(null);

            if (department == null) {
                throw new IllegalArgumentException("El departamento seleccionado no es válido.");
            }

            representative.setIdDepartment(String.valueOf(department.getDepartmentId()));

            boolean success = representativeService.updateRepresentative(representative);

            if (success) {
                statusLabel.setText("¡Representante actualizado exitosamente!");
                statusLabel.setTextFill(Color.GREEN);

                if (parentController != null) {
                    parentController.loadOrganizationData();
                }
            } else {
                statusLabel.setText("No se pudo actualizar el representante.");
                statusLabel.setTextFill(Color.RED);
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
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("23000")) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al actualizar el representante.");
                LOGGER.error("Error al actualizar el representante: {}", e);
            }
        } catch (IllegalArgumentException e) {
            statusLabel.setText("Argumento inválido:");
            statusLabel.setTextFill(Color.RED);
            LOGGER.warn("Error al guardar cambios: {}", e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al guardar cambios.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al guardar cambios: {}", e);
        }
    }

    private boolean areFieldsFilled() {
        return !namesField.getText().isEmpty() &&
                !surnamesField.getText().isEmpty() &&
                !emailField.getText().isEmpty() &&
                organizationBox.getValue() != null &&
                departmentBox.getValue() != null;
    }
}