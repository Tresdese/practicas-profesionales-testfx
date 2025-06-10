package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import logic.DTO.LinkedOrganizationDTO;
import logic.DTO.RepresentativeDTO;
import logic.exceptions.InvalidData;
import logic.exceptions.RepeatedId;
import logic.services.LinkedOrganizationService;
import logic.services.RepresentativeService;
import logic.services.ServiceConfig;
import logic.exceptions.EmptyFields;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class GUI_RegisterRepresentativeController {

    private static final Logger logger = LogManager.getLogger(GUI_RegisterRepresentativeController.class);

    @FXML
    private Button buttonRegisterUser;

    @FXML
    private TextField emailField;

    @FXML
    private Label label;

    @FXML
    private TextField nameField;

    @FXML
    private ChoiceBox<String> organizationBox;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField surnameField;

    private GUI_CheckRepresentativeListController parentController;

    private LinkedOrganizationService linkedOrganizationService;
    private RepresentativeService representativeService;

    public void setParentController(GUI_CheckRepresentativeListController parentController) {
        this.parentController = parentController;
    }

    @FXML
    public void initialize() {
        try {
            ServiceConfig serviceConfig = new ServiceConfig();
            representativeService = serviceConfig.getRepresentativeService();
            linkedOrganizationService = serviceConfig.getLinkedOrganizationService();

            for (String organizationName : getOrganizationNames()) {
                organizationBox.getItems().add(organizationName);
            }
        } catch (SQLException e) {
            logger.error("Error al inicializar los servicios: {}", e.getMessage(), e);
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
            String organization = organizationBox.getValue();

            LinkedOrganizationDTO linkedOrganization = linkedOrganizationService.searchLinkedOrganizationByName(organization);
            if (linkedOrganization == null || linkedOrganization.getIdOrganization() == null) {
                throw new InvalidData("La organización seleccionada no es válida.");
            }

            String organizationId = linkedOrganization.getIdOrganization();

            RepresentativeDTO representative = new RepresentativeDTO("0", names, surname, email, organizationId);

            try {
                boolean success = representativeService.registerRepresentative(representative);

                if (success) {
                    statusLabel.setText("¡Representante registrado exitosamente!");
                    statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);

                    organizationBox.getItems().clear();
                    organizationBox.getItems().addAll(getOrganizationNames());
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getOrganizationNames() {
        ObservableList<LinkedOrganizationDTO> organizationList = FXCollections.observableArrayList();
        try {
            List<LinkedOrganizationDTO> organizations = linkedOrganizationService.getAllLinkedOrganizations();
            organizationList.addAll(organizations);
        } catch (SQLException e) {
            statusLabel.setText("Error al cargar los datos de las organizaciones.");
            logger.error("Error al cargar los datos de las organizaciones: {}", e.getMessage(), e);
        }
        return organizationList.stream()
                .map(LinkedOrganizationDTO::getName)
                .toList();
    }

    public boolean areFieldsFilled() {
        return !nameField.getText().isEmpty() &&
                !surnameField.getText().isEmpty() &&
                !emailField.getText().isEmpty() &&
                organizationBox.getValue() != null;
    }
}
