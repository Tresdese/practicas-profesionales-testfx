package gui;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private Label statusLabel;

    private GUI_CheckRepresentativeListController parentController;

    private RepresentativeDTO representative;
    private RepresentativeService representativeService;
    private LinkedOrganizationService linkedOrganizationService;

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
            String orgId = representative.getIdOrganization();
            if (orgId != null && !orgId.isEmpty()) {
                LinkedOrganizationDTO organization = linkedOrganizationService.searchLinkedOrganizationById(orgId);
                if (organization != null) {
                    organizationBox.setValue(organization.getName());
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener la organización del representante: {}", e.getMessage(), e);
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
            String organizationName = organizationBox.getValue();

            representative.setNames(name);
            representative.setSurnames(surname);
            representative.setEmail(email);

            LinkedOrganizationDTO linkedOrganization = linkedOrganizationService.searchLinkedOrganizationByName(organizationName);
            if (linkedOrganization == null || linkedOrganization.getIdOrganization() == null) {
                throw new IllegalArgumentException("La organización seleccionada no es válida.");
            }

            representative.setIdOrganization(linkedOrganization.getIdOrganization());

            boolean success = representativeService.updateRepresentative(representative);

            if (success) {
                statusLabel.setText("¡Representante actualizado exitosamente!");
                statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);

                if (parentController != null) {
                    parentController.loadOrganizationData();
                }
            } else {
                statusLabel.setText("No se pudo actualizar el representante.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            }
        } catch (Exception e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            logger.error("Error: {}", e.getMessage(), e);
        }
    }

    private boolean areFieldsFilled() {
        return !namesField.getText().isEmpty() &&
                !surnamesField.getText().isEmpty() &&
                !emailField.getText().isEmpty() &&
                organizationBox.getValue() != null;
    }
}