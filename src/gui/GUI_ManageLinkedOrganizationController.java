package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.stage.Stage;
import logic.DTO.LinkedOrganizationDTO;
import logic.services.LinkedOrganizationService;
import logic.services.ServiceConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.io.IOException;

public class GUI_ManageLinkedOrganizationController {

    private static final Logger logger = LogManager.getLogger(GUI_ManageLinkedOrganizationController.class);

    @FXML
    private TextField nameField, addressField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button registerDepartmentButton;

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

        nameField.setText(organization.getName() != null ? organization.getName() : "");
        addressField.setText(organization.getAddress() != null ? organization.getAddress() : "");
    }

    @FXML
    private void handleSaveChanges() {
        try {
            if (!areFieldsFilled()) {
                throw new IllegalArgumentException("Todos los campos deben estar llenos.");
            }

            String name = nameField.getText();
            String address = addressField.getText();

            organization.setName(name);
            organization.setAddress(address);

            organizationService.updateOrganization(organization);

            statusLabel.setText("¡Organización vinculada actualizada exitosamente!");
            statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);

            if (parentController != null) {
                parentController.loadOrganizationData();
            }

        } catch (SQLException e) {
            statusLabel.setText("Error al actualizar la organización en la base de datos.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            logger.error("Error SQL: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleRegisterDepartament() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_RegisterDepartment.fxml"));
            Parent root = loader.load();

            GUI_RegisterDepartmentController controller = loader.getController();
            if (organization != null) {
                try {
                    int orgId = Integer.parseInt(organization.getIdOrganization());
                    controller.setOrganizationId(orgId);
                } catch (NumberFormatException e) {
                    logger.error("ID de organización no es un número válido: {}", organization.getIdOrganization());
                }
            }
            Stage stage = new Stage();
            stage.setTitle("Registrar Departamento");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.error("Error al abrir la ventana de registro de departamento: {}", e.getMessage(), e);
        }
    }

    private boolean areFieldsFilled() {
        return !nameField.getText().isEmpty() &&
                !addressField.getText().isEmpty();
    }
}