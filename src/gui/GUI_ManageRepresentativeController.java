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

    private static final Logger logger = LogManager.getLogger(GUI_ManageRepresentativeController.class);

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
                List<String> departmentNames = departments.stream()
                        .map(DepartmentDTO::getName)
                        .toList();
                departmentBox.getItems().setAll(departmentNames);
            }
        } catch (Exception e) {
            logger.error("Error al cargar departamentos: {}", e.getMessage(), e);
        }
    }

    public void setRepresentativeData(RepresentativeDTO representative) {
        if (representative == null) {
            logger.error("El objeto RepresentativeDTO es nulo.");
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
                    // Selecciona la organización y carga los departamentos
                    LinkedOrganizationDTO org = linkedOrganizationService.searchLinkedOrganizationById(String.valueOf(department.getOrganizationId()));
                    if (org != null) {
                        organizationBox.setValue(org.getName());
                        loadDepartmentsForSelectedOrganization();
                        departmentBox.setValue(department.getName());
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener el departamento del representante: {}", e.getMessage(), e);
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

            // Buscar el departamento por nombre y organización seleccionada
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
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(Color.RED);
            logger.error("Error: {}", e.getMessage(), e);
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