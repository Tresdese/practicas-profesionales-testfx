package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import javafx.stage.Stage;
import logic.DTO.LinkedOrganizationDTO;
import logic.services.LinkedOrganizationService;
import logic.services.ServiceConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.io.IOException;

public class GUI_ManageLinkedOrganizationController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_ManageLinkedOrganizationController.class);

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
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al inicializar el servicio de organización.");
                LOGGER.error("Error al inicializar el servicio de organización: {}", e.getMessage(), e);
                statusLabel.setTextFill(Color.RED);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al inicializar el servicio de organización.");
            LOGGER.error("Error inesperado al inicializar el servicio de organización:0 {}", e.getMessage(), e);
            statusLabel.setTextFill(Color.RED);
        }
    }

    public void setOrganizationData(LinkedOrganizationDTO organization) {
        if (organization == null) {
            LOGGER.error("El objeto LinkedOrganizationDTO es nulo.");
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
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos desconocida. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad. Verifique los datos ingresados.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al actualizar la organización vinculada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al actualizar la organización vinculada: {}", e);
            }
        } catch (IllegalArgumentException e) {
            statusLabel.setText("Error de validación");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al guardar cambios: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al guardar cambios: {}", e.getMessage(), e);
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
                    LOGGER.error("ID de organización no es un número válido: {}", organization.getIdOrganization());
                }
            }
            Stage stage = new Stage();
            stage.setTitle("Registrar Departamento");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.error("Error al abrir la ventana de registro de departamento: {}", e.getMessage(), e);
            statusLabel.setText("Error al abrir la ventana de registro de departamento.");
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al abrir la ventana de registro de departamento: {}", e.getMessage(), e);
            statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    private boolean areFieldsFilled() {
        return !nameField.getText().isEmpty() &&
                !addressField.getText().isEmpty();
    }
}