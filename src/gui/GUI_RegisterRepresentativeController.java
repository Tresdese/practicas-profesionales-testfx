package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

import java.sql.SQLException;
import java.util.List;

public class GUI_RegisterRepresentativeController {

    private static final Logger logger = LogManager.getLogger(GUI_RegisterRepresentativeController.class);

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
            logger.error("Error al inicializar los servicios: {}", e.getMessage(), e);
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
            statusLabel.setText("Error al cargar departamentos.");
            logger.error("Error al cargar departamentos: {}", e.getMessage(), e);
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
                statusLabel.setText("No se pudo conectar a la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                logger.error("Error de SQL al registrar el representante: {}", e.getMessage(), e);
            }

            if (parentController != null) {
                parentController.loadOrganizationData();
            }
        } catch (EmptyFields | InvalidData | RepeatedId e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            logger.error("Error: {}", e.getMessage(), e);
        }
    }

    public List<LinkedOrganizationDTO> getOrganizations() {
        try {
            return linkedOrganizationService.getAllLinkedOrganizations();
        } catch (SQLException e) {
            statusLabel.setText("Error al cargar los datos de las organizaciones.");
            logger.error("Error al cargar los datos de las organizaciones: {}", e.getMessage(), e);
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