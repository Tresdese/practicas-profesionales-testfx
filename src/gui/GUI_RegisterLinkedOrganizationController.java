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

import java.io.IOException;
import java.sql.SQLException;

public class GUI_RegisterLinkedOrganizationController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_RegisterLinkedOrganizationController.class);

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
           String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: ", e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: ", e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                statusLabel.setText("Tabla de organizaciones no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de organizaciones no encontrada: ", e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                statusLabel.setText("Columna no encontrada en la tabla de organizaciones.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada en la tabla de organizaciones: ", e);
            } else if (sqlState != null && sqlState.equals("HY000")) {
                statusLabel.setText("Error general de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos: ", e);
            } else if (sqlState != null && sqlState.equals("42000")){
                statusLabel.setText("Base de datos desconocida. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: ", e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: ", e.getMessage(), e);
            } else {
                statusLabel.setText("Error de base de datos al inicializar el servicio de organización.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de base de datos al inicializar el servicio de organización: ", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al leer la configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al leer la configuración de la base de datos: ", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error al inicializar el servicio de organización.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al inicializar el servicio de organización: ", e.getMessage(), e);
        }

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

            LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, name, address, 1);
            String generatedId = organizationService.registerOrganization(organization);
            organization.setIdOrganization(generatedId);

            statusLabel.setText("¡Organización registrada exitosamente!");
            statusLabel.setTextFill(Color.GREEN);

            if (parentController != null) {
                parentController.loadOrganizationData();
            }

        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                statusLabel.setText("Tabla de organizaciones no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de organizaciones no encontrada: {}", e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                statusLabel.setText("Columna no encontrada en la tabla de organizaciones.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada en la tabla de organizaciones: {}", e);
            } else if (sqlState != null && sqlState.equals("HY000")) {
                statusLabel.setText("Error general de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos: {}", e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage());
            } else if (sqlState != null && sqlState.equals("23000")) {
                statusLabel.setText("Violación de restricción de integridad. Verifique los datos ingresados.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e);
            } else {
                statusLabel.setText("Error al registrar la organización.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al registrar la organización: {}", e);
            }
        } catch (EmptyFields e) {
            LOGGER.warn("Error de validación: {}", e);
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(Color.RED);
        } catch (IOException e) {
            LOGGER.error("Error de entrada/salida al leer la configuración de la base de datos: {}", e);
            statusLabel.setText("Error al leer la configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            LOGGER.error("Error inesperado: {}", e);
            statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    public boolean areFieldsFilled() {
        return !nameField.getText().isEmpty() &&
                !addressField.getText().isEmpty();
    }
}