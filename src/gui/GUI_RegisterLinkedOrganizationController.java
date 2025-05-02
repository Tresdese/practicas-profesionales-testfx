package gui;

import data_access.ConecctionDataBase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DTO.LinkedOrganizationDTO;
import logic.DTO.RepresentativeDTO;
import logic.exceptions.*;
import logic.services.LinkedOrganizationService;
import logic.services.RepresentativeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class GUI_RegisterLinkedOrganizationController {

    private static final Logger logger = LogManager.getLogger(GUI_RegisterLinkedOrganizationController.class);

    @FXML
    private Label statusLabel;

    @FXML
    private TextField fieldName, fieldAddress, fieldRepresentativeName, fieldRepresentativeSurname, fieldRepresentativeEmail;

    private GUI_CheckListLinkedOrganizationController parentController;

    public void setParentController(GUI_CheckListLinkedOrganizationController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleRegisterLinkedOrganization() {
        try {
            if (!areFieldsFilled()) {
                throw new EmptyFields("Todos los campos deben estar llenos.");
            }

            String name = fieldName.getText();
            String address = fieldAddress.getText();
            String representativeName = fieldRepresentativeName.getText();
            String representativeSurnames = fieldRepresentativeSurname.getText();
            String representativeEmail = fieldRepresentativeEmail.getText();

            ConecctionDataBase connectionDB = new ConecctionDataBase();
            try (Connection connection = connectionDB.connectDB()) {
                LinkedOrganizationService organizationService = new LinkedOrganizationService(connection);
                RepresentativeService representativeService = new RepresentativeService(connection);

                LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, name, address);
                String generatedId = organizationService.registerOrganization(organization);
                organization.setIddOrganization(generatedId);

                RepresentativeDTO representative = new RepresentativeDTO(
                        null,
                        representativeName,
                        representativeSurnames,
                        representativeEmail,
                        organization.getIddOrganization()
                );
                representativeService.registerRepresentative(representative);

                statusLabel.setText("¡Organización y representante registrados exitosamente!");
                statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);

//                if (parentController != null) {
//                    parentController.loadOrganizationData();
//                }
            } catch (SQLException e) {
                logger.error("Error de SQL al registrar la organización o el representante: {}", e.getMessage(), e);
                statusLabel.setText("Error de conexión con la base de datos. Intente más tarde.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            } finally {
                connectionDB.closeConnection();
            }
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
                !fieldAddress.getText().isEmpty() &&
                !fieldRepresentativeName.getText().isEmpty() &&
                !fieldRepresentativeSurname.getText().isEmpty() &&
                !fieldRepresentativeEmail.getText().isEmpty();
    }
}
