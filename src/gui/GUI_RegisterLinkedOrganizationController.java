package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import logic.DTO.LinkedOrganizationDTO;
import logic.exceptions.EmptyFields;
import logic.services.LinkedOrganizationService;
import logic.services.ServiceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class GUI_RegisterLinkedOrganizationController {

    private static final Logger logger = LogManager.getLogger(GUI_RegisterLinkedOrganizationController.class);

    private static final int MAX_NAME = 100;
    private static final int MAX_ADDRESS = 150;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField nameField, addressField;

    @FXML
    private Label nameCharCountLabel, addressCharCountLabel;

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

        // Limitar caracteres y mostrar contador
        nameField.setTextFormatter(createTextFormatter(MAX_NAME));
        addressField.setTextFormatter(createTextFormatter(MAX_ADDRESS));
        configureCharCount(nameField, nameCharCountLabel, MAX_NAME);
        configureCharCount(addressField, addressCharCountLabel, MAX_ADDRESS);
    }

    private TextFormatter<String> createTextFormatter(int maxLength) {
        return new TextFormatter<>(change ->
                change.getControlNewText().length() <= maxLength ? change : null
        );
    }

    private void configureCharCount(TextField textField, Label charCountLabel, int maxLength) {
        if (charCountLabel == null) return;
        charCountLabel.setText("0/" + maxLength);
        textField.textProperty().addListener((obs, oldText, newText) ->
                charCountLabel.setText(newText.length() + "/" + maxLength)
        );
    }

    @FXML
    private void handleRegisterLinkedOrganization() {
        try {
            if (!areFieldsFilled()) {
                throw new EmptyFields("Todos los campos deben estar llenos.");
            }

            String name = nameField.getText();
            String address = addressField.getText();

            LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, name, address);
            String generatedId = organizationService.registerOrganization(organization);
            organization.setIdOrganization(generatedId);

            statusLabel.setText("¡Organización registrada exitosamente!");
            statusLabel.setTextFill(Color.GREEN);

            if (parentController != null) {
                parentController.loadOrganizationData();
            }

        } catch (SQLException e) {
            logger.error("Error de SQL al registrar la organización: {}", e.getMessage(), e);
            statusLabel.setText("Error de conexión con la base de datos. Intente más tarde.");
            statusLabel.setTextFill(Color.RED);
        } catch (EmptyFields e) {
            logger.warn("Error de validación: {}", e.getMessage(), e);
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    public boolean areFieldsFilled() {
        return !nameField.getText().isEmpty() &&
                !addressField.getText().isEmpty();
    }
}