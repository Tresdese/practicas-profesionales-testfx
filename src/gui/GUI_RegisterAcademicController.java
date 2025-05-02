package gui;

import data_access.ConecctionDataBase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DAO.UserDAO;
import logic.DTO.Role;
import logic.DTO.UserDTO;
import logic.exceptions.*;
import logic.utils.StaffNumberValidator;
import logic.utils.PasswordHasher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class GUI_RegisterAcademicController {

    private static final Logger logger = LogManager.getLogger(GUI_RegisterAcademicController.class);

    @FXML
    private Label label;
    private Label roleLabel;

    @FXML
    private ChoiceBox<String> roleBox;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField fieldNumberOffStaff, fieldNames, fieldSurnames, fieldUser, fieldPasswordVisible, fieldConfirmPasswordVisible;

    @FXML
    private PasswordField fieldPassword, fieldConfirmPassword;

    @FXML
    private Button togglePasswordVisibility;

    private boolean isPasswordVisible = false;

    @FXML
    public void initialize() {
        togglePasswordVisibility.setText("🙈");
        togglePasswordVisibility.setOnAction(event -> togglePasswordVisibility());

        roleBox.getItems().addAll("Académico", "Académico Evaluador", "Coordinador");
    }

    @FXML
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {

            fieldPassword.setText(fieldPasswordVisible.getText());
            fieldConfirmPassword.setText(fieldConfirmPasswordVisible.getText());

            fieldPasswordVisible.setVisible(false);
            fieldPasswordVisible.setManaged(false);
            fieldConfirmPasswordVisible.setVisible(false);
            fieldConfirmPasswordVisible.setManaged(false);

            fieldPassword.setVisible(true);
            fieldPassword.setManaged(true);
            fieldConfirmPassword.setVisible(true);
            fieldConfirmPassword.setManaged(true);

            togglePasswordVisibility.setText("🙈");
        } else {

            fieldPasswordVisible.setText(fieldPassword.getText());
            fieldConfirmPasswordVisible.setText(fieldConfirmPassword.getText());

            fieldPassword.setVisible(false);
            fieldPassword.setManaged(false);
            fieldConfirmPassword.setVisible(false);
            fieldConfirmPassword.setManaged(false);

            fieldPasswordVisible.setVisible(true);
            fieldPasswordVisible.setManaged(true);
            fieldConfirmPasswordVisible.setVisible(true);
            fieldConfirmPasswordVisible.setManaged(true);

            togglePasswordVisibility.setText("👁");
        }
        isPasswordVisible = !isPasswordVisible;
    }

    @FXML
    private void handleRegisterAcademic() {
        try {
            if (!areFieldsFilled()) {
                throw new EmptyFields("Todos los campos deben estar llenos.");
            }
            String numberOffStaff = fieldNumberOffStaff.getText();
            StaffNumberValidator.validate(numberOffStaff);

            String names = fieldNames.getText();
            String surname = fieldSurnames.getText();
            String selectedRoleText = roleBox.getValue();
            Role role = getRoleFromText(selectedRoleText);
            String userName = fieldUser.getText();
            String password = isPasswordVisible ? fieldPasswordVisible.getText() : fieldPassword.getText();

            String hashedPassword = PasswordHasher.hashPassword(password);

            UserDTO academic = new UserDTO("0", numberOffStaff, names, surname, userName, hashedPassword, role);

            ConecctionDataBase connectionDB = new ConecctionDataBase();
            try (Connection connection = connectionDB.connectDB()) {
                UserDAO userDAO = new UserDAO();

                if (userDAO.idIsRegistered(numberOffStaff, connection)) {
                    throw new RepeatedId("El numero de personal ya está registrado.");
                }

                boolean success = userDAO.insertUser(academic, connection);

                if (success) {
                    statusLabel.setText("¡Académico registrado exitosamente!");
                    statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
                } else {
                    statusLabel.setText("El académico ya existe.");
                    statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                }
            } catch (SQLException e) {
                statusLabel.setText("No se pudo conectar a la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                logger.error("Error de SQL al registrar el académico: {}", e.getMessage(), e);
            } finally {
                connectionDB.closeConnection();
            }
        } catch (EmptyFields | InvalidData | RepeatedId | RepeatedPhone | RepeatedEmail e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            logger.error("Error: {}", e.getMessage(), e);
        }
    }

    public boolean areFieldsFilled() {
        return !fieldNumberOffStaff.getText().isEmpty() &&
                !fieldNames.getText().isEmpty() &&
                !fieldSurnames.getText().isEmpty() &&
                !fieldUser.getText().isEmpty() &&
                (!fieldPassword.getText().isEmpty() || !fieldPasswordVisible.getText().isEmpty()) &&
                (!fieldConfirmPassword.getText().isEmpty() || !fieldConfirmPasswordVisible.getText().isEmpty());
    }

    private Role getRoleFromText(String text) {
        switch (text) {
            case "Académico":
                return Role.ACADEMICO;
            case "Académico Evaluador":
                return Role.ACADEMICO_EVALUADOR;
            case "Coordinador":
                return Role.COORDINADOR;
            default:
                throw new IllegalArgumentException("Rol no válido: " + text);
        }
    }
}