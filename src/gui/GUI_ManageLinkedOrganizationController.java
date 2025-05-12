package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import logic.DTO.LinkedOrganizationDTO;
import logic.services.LinkedOrganizationService;
import logic.services.ServiceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class GUI_ManageLinkedOrganizationController {

    private static final Logger logger = LogManager.getLogger(GUI_ManageLinkedOrganizationController.class);

    @FXML
    private TextField fieldName, fieldAddress;

    @FXML
    private Label statusLabel;

    private GUI_CheckListLinkedOrganizationController parentController;

    private LinkedOrganizationDTO organization;
    private LinkedOrganizationService organizationService;

    public void setParentController(GUI_CheckListLinkedOrganizationController parentController) {
        this.parentController = parentController;
    }

    public void setOrganizationService(LinkedOrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @FXML
    public void initialize() {
        try {
            ServiceConfig serviceConfig = new ServiceConfig();
            organizationService = serviceConfig.getLinkedOrganizationService();
        } catch (SQLException e) {
            logger.error("Error al inicializar el servicio de organización: {}", e.getMessage(), e);
        }
    }

    public void setOrganizationData(LinkedOrganizationDTO organization) {
        if (organization == null) {
            logger.error("El objeto LinkedOrganizationDTO es nulo.");
            return;
        }

        this.organization = organization;

        fieldName.setText(organization.getName() != null ? organization.getName() : "");
        fieldAddress.setText(organization.getAddress() != null ? organization.getAddress() : "");
    }

    @FXML
    private void handleSaveChanges() {
        try {
            if (!areFieldsFilled()) {
                throw new IllegalArgumentException("Todos los campos deben estar llenos.");
            }

            String name = fieldName.getText();
            String address = fieldAddress.getText();

            organization.setName(name);
            organization.setAddress(address);

            organizationService.updateOrganization(organization);

            statusLabel.setText("¡Organización vinculada actualizada exitosamente!");
            statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);

            if (parentController != null) {
                parentController.loadOrganizationData();
            }

        } catch (Exception e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            logger.error("Error: {}", e.getMessage(), e);
        }
    }

    private boolean areFieldsFilled() {
        return !fieldName.getText().isEmpty() &&
                !fieldAddress.getText().isEmpty();
    }
}