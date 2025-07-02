package gui;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GUI_ManageRepresentativeController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_ManageRepresentativeController.class);

    @FXML
    private TextField namesField, surnamesField, emailField;

    @FXML
    private ChoiceBox<String> organizationChoiceBox;

    @FXML
    private ChoiceBox<String> departmentChoiceBox;

    @FXML
    private Label statusLabel;

    @FXML
    private Button saveButton;

    private GUI_CheckRepresentativeListController parentController;

    private RepresentativeDTO representative;
    private RepresentativeService representativeService;
    private LinkedOrganizationService linkedOrganizationService;
    private DepartmentDAO departmentDAO = new DepartmentDAO();

    private String originalNames = "";
    private String originalSurnames = "";
    private String originalEmail = "";
    private String originalOrganization = "";
    private String originalDepartment = "";

    private boolean isLoadingData = false;

    private final ChangeListener<Object> changeListener = (obs, oldVal, newVal) -> {
        if (!isLoadingData) {
            checkIfChanged();
        }
    };

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

            organizationChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (!isLoadingData) {
                    loadDepartmentsForSelectedOrganization();
                    checkIfChanged();
                }
            });
            departmentChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (!isLoadingData) {
                    checkIfChanged();
                }
            });

            setButtons();
            if (saveButton != null) {
                saveButton.setDisable(true);
            }
        } catch (SQLException e) {
            handleSQLException(e, "Error en la base de datos al inicializar el servicio.");
        } catch (IOException e) {
            statusLabel.setText("Error al cargar la configuración del servicio.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al cargar la configuración del servicio: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al inicializar el servicio.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al inicializar el servicio: {}", e.getMessage(), e);
        }
    }

    private void setButtons() {
        if (saveButton != null) {
            saveButton.setOnAction(event -> handleSaveChanges());
            saveButton.setDisable(true);
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
            handleSQLException(e, "Error al cargar organizaciones.");
        } catch (IOException e) {
            statusLabel.setText("Error al cargar la configuración del servicio.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al cargar la configuración del servicio: {}", e.getMessage(), e);
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
                List<String> departmentNames = departments.stream()
                        .map(DepartmentDTO::getName)
                        .toList();
                departmentChoiceBox.getItems().setAll(departmentNames);
            }
        } catch (SQLException e) {
            handleSQLException(e, "Error al cargar departamentos.");
        } catch (IOException e) {
            statusLabel.setText("Error al cargar la configuración del servicio.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al cargar la configuración del servicio: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error al cargar departamentos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al cargar departamentos: {}", e.getMessage(), e);
        }
    }

    public void setRepresentativeData(RepresentativeDTO representative) {
        isLoadingData = true;
        removeFieldListeners();

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
                        organizationChoiceBox.setValue(org.getName());
                        loadDepartmentsForSelectedOrganization();
                        departmentChoiceBox.setValue(department.getName());
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, "Error al cargar los datos del representante.");
        } catch (IOException e) {
            statusLabel.setText("Error al cargar la configuración del servicio.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al cargar la configuración del servicio: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar los datos del representante.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al cargar los datos del representante: {}", e.getMessage(), e);
        }

        setOriginalValues();
        addFieldListeners();
        isLoadingData = false;
        checkIfChanged();
    }

    private void setOriginalValues() {
        originalNames = namesField.getText();
        originalSurnames = surnamesField.getText();
        originalEmail = emailField.getText();
        originalOrganization = organizationChoiceBox.getValue();
        originalDepartment = departmentChoiceBox.getValue();
    }

    private void addFieldListeners() {
        namesField.textProperty().addListener(changeListener);
        surnamesField.textProperty().addListener(changeListener);
        emailField.textProperty().addListener(changeListener);
        organizationChoiceBox.valueProperty().addListener(changeListener);
        departmentChoiceBox.valueProperty().addListener(changeListener);
    }

    private void removeFieldListeners() {
        namesField.textProperty().removeListener(changeListener);
        surnamesField.textProperty().removeListener(changeListener);
        emailField.textProperty().removeListener(changeListener);
        organizationChoiceBox.valueProperty().removeListener(changeListener);
        departmentChoiceBox.valueProperty().removeListener(changeListener);
    }

    private void checkIfChanged() {
        boolean changed =
                !safeEquals(namesField.getText(), originalNames) ||
                        !safeEquals(surnamesField.getText(), originalSurnames) ||
                        !safeEquals(emailField.getText(), originalEmail) ||
                        !safeEquals(organizationChoiceBox.getValue(), originalOrganization) ||
                        !safeEquals(departmentChoiceBox.getValue(), originalDepartment);

        if (saveButton != null) {
            saveButton.setDisable(!changed);
        }
    }

    private boolean safeEquals(String a, String b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }

    @FXML
    private void handleSaveChanges() {
        try {
            if (!areFieldsFilled()) {
                throw new IllegalArgumentException("Todos los campos deben estar llenos.");
            }

            representative.setNames(namesField.getText());
            representative.setSurnames(surnamesField.getText());
            representative.setEmail(emailField.getText());

            String orgName = organizationChoiceBox.getValue();
            String departmentName = departmentChoiceBox.getValue();

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

                setOriginalValues();
                if (saveButton != null) {
                    saveButton.setDisable(true);
                }
                if (parentController != null) {
                    parentController.loadOrganizationData();
                }
            } else {
                statusLabel.setText("No se pudo actualizar el representante.");
                statusLabel.setTextFill(Color.RED);
            }
        } catch (SQLException e) {
            handleSQLException(e, "Error de base de datos al actualizar el representante.");
        } catch (IllegalArgumentException e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(Color.RED);
            LOGGER.warn("Error al guardar cambios: {}", e.getMessage(), e);
        } catch (IOException e) {
            statusLabel.setText("Error al cargar la configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al cargar la configuración de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al guardar cambios.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al guardar cambios: {}", e.getMessage(), e);
        }
    }

    private boolean areFieldsFilled() {
        return !namesField.getText().isEmpty() &&
                !surnamesField.getText().isEmpty() &&
                !emailField.getText().isEmpty() &&
                organizationChoiceBox.getValue() != null &&
                departmentChoiceBox.getValue() != null;
    }

    private void handleSQLException(SQLException e, String defaultMsg) {
        String sqlState = e.getSQLState();
        if ("08001".equals(sqlState)) {
            statusLabel.setText("Error de conexión con la base de datos.");
        } else if ("08S01".equals(sqlState)) {
            statusLabel.setText("Conexión interrumpida con la base de datos.");
        } else if ("42S02".equals(sqlState)) {
            statusLabel.setText("Tabla no encontrada en la base de datos.");
        } else if ("42S22".equals(sqlState)) {
            statusLabel.setText("Columna no encontrada en la base de datos.");
        } else if ("HY000".equals(sqlState)) {
            statusLabel.setText("Error general de la base de datos.");
        } else if ("42000".equals(sqlState)) {
            statusLabel.setText("Base de datos desconocida.");
        } else if ("28000".equals(sqlState)) {
            statusLabel.setText("Acceso denegado a la base de datos.");
        } else if ("23000".equals(sqlState)) {
            statusLabel.setText("Violación de restricción de integridad.");
        } else {
            statusLabel.setText(defaultMsg);
        }
        statusLabel.setTextFill(Color.RED);
        LOGGER.error("{}: {}", defaultMsg, e.getMessage(), e);
    }
}