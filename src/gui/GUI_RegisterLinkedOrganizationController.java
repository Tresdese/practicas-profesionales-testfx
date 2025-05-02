package gui;

import data_access.ConecctionDataBase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DAO.LinkedOrganizationDAO;
import logic.DTO.LinkedOrganizationDTO;
import logic.exceptions.*;
import logic.utils.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class GUI_RegisterLinkedOrganizationController {
    private static final Logger logger = LogManager.getLogger(GUI_RegisterStudentController.class);

    @FXML
    private Label statusLabel;

    @FXML
    private TextField fieldName, fieldAddress, fieldRepresentativeName, fieldRepresentativeSurnames, fieldRepresentativeEmail;

    private GUI_CheckListOfStudentsController parentController; // Referencia al controlador de la tabla

    public void setParentController(GUI_CheckListOfStudentsController parentController) {
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

            LinkedOrganizationDTO organization = new LinkedOrganizationDTO("0", name, address);

            ConecctionDataBase connectionDB = new ConecctionDataBase();
            try (Connection connection = connectionDB.connectDB()) {
                LinkedOrganizationDAO linkedOrganizationDAO = new LinkedOrganizationDAO();

                if (linkedOrganizationDAO.isTuitonRegistered(tuiton, connection)) {
                    throw new RepeatedTuiton("La matrícula ya está registrada.");
                }

                if (linkedOrganizationDAO.isPhoneRegistered(phone, connection)) {
                    throw new RepeatedPhone("El número de teléfono ya está registrado.");
                }

                if (linkedOrganizationDAO.isEmailRegistered(email, connection)) {
                    throw new RepeatedEmail("El correo electrónico ya está registrado.");
                }

                boolean success = linkedOrganizationDAO.insertStudent(student, connection);

                if (success) {
                    statusLabel.setText("¡Organizacion registrada exitosamente!");
                    statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);

                    if (parentController != null) {
                        parentController.loadStudentData();
                    }
                } else {
                    statusLabel.setText("No se pudo registrar la organizacion.");
                    statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                }
            } catch (SQLException e) {
                logger.error("Error de SQL al registrar la organizacion: {}", e.getMessage(), e);
                statusLabel.setText("Error de conexión con la base de datos. Intente más tarde.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            } finally {
                connectionDB.closeConnection();
            }
        } catch (EmptyFields | InvalidData | RepeatedTuiton | RepeatedPhone | RepeatedEmail | PasswordDoesNotMatch e) {
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
        return !fieldName.getText().isEmpty()

    }
}
