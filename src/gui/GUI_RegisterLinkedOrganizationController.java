package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DTO.LinkedOrganizationDTO;
import logic.exceptions.EmptyFields;
import logic.services.LinkedOrganizationService;
import logic.services.ServiceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class GUI_RegisterLinkedOrganizationController {

    private static final Logger logger = LogManager.getLogger(GUI_RegisterLinkedOrganizationController.class);

    @FXML
    private Label statusLabel;

    @FXML
    private TextField fieldName, fieldAddress;

    private GUI_CheckListLinkedOrganizationController parentController;

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


    @FXML
    private void handleRegisterLinkedOrganization() {
        try {
            if (!areFieldsFilled()) {
                throw new EmptyFields("Todos los campos deben estar llenos.");
            }

            String name = fieldName.getText();
            String address = fieldAddress.getText();

            LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, name, address);
            String generatedId = organizationService.registerOrganization(organization);
            organization.setIddOrganization(generatedId);

            statusLabel.setText("¡Organización registrada exitosamente!");
            statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);

            if (parentController != null) {
                parentController.loadOrganizationData();
            }

        } catch (SQLException e) {
            logger.error("Error de SQL al registrar la organización: {}", e.getMessage(), e);
            statusLabel.setText("Error de conexión con la base de datos. Intente más tarde.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        } catch (EmptyFields e) {
            logger.warn("Error de validación: {}", e.getMessage(), e);
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    public boolean areFieldsFilled() {
        return !fieldName.getText().isEmpty() &&
                !fieldAddress.getText().isEmpty();
    }
}