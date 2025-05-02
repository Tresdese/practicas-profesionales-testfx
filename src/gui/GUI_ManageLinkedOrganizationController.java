package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import logic.DAO.LinkedOrganizationDAO;
import logic.DTO.LinkedOrganizationDTO;
import logic.DTO.StudentDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class GUI_ManageLinkedOrganizationController {

    private static final Logger logger = LogManager.getLogger(GUI_ManageLinkedOrganizationController.class);

    @FXML
    private TextField fieldName, fieldAddress;

    @FXML
    private Label statusLabel;

    private LinkedOrganizationDTO organization;

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
        try (Connection connection = new data_access.ConecctionDataBase().connectDB()) {
            LinkedOrganizationDAO organizationDAO = new LinkedOrganizationDAO(connection);

            if (!areFieldsFilled()) {
                throw new IllegalArgumentException("Todos los campos deben estar llenos.");
            }

            String name = fieldName.getText();
            String address = fieldAddress.getText();

            organization.setName(name);
            organization.setAddress(address);

            boolean success = organizationDAO.updateLinkedOrganization(organization);
            if (success) {
                statusLabel.setText("¡Organización vinculada actualizada exitosamente!");
                statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            } else {
                throw new Exception("No se pudo actualizar la organización vinculada.");
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