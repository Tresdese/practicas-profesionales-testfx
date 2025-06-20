package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import logic.DTO.Role;
import logic.DTO.UserDTO;
import logic.exceptions.*;
import logic.services.ServiceConfig;
import logic.services.UserService;
import logic.utils.StaffNumberValidator;
import logic.utils.PasswordHasher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class GUI_RegisterAcademicController {

    private static final Logger logger = LogManager.getLogger(GUI_RegisterAcademicController.class);

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
    private GUI_CheckAcademicListController parentController;

    private UserService userService;

    public void setParentController(GUI_CheckAcademicListController parentController) {
        this.parentController = parentController;
    }

    @FXML
    public void initialize() {
        togglePasswordVisibility.setText("🙈");
        togglePasswordVisibility.setOnAction(event -> togglePasswordVisibility());

        roleBox.getItems().addAll("Académico", "Académico Evaluador", "Coordinador");

        try {
            ServiceConfig serviceConfig = new ServiceConfig();
            userService = serviceConfig.getUserService();
        } catch (SQLException e) {
            statusLabel.setText("Error al conectar a la base de datos.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error al inicializar el servicio de usuarios: {}", e.getMessage(), e);
        }
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

            String password = isPasswordVisible ? fieldPasswordVisible.getText() : fieldPassword.getText();
            String confirmPassword = isPasswordVisible ? fieldConfirmPasswordVisible.getText() : fieldConfirmPassword.getText();

            if (!password.equals(confirmPassword)) {
                statusLabel.setText("Las contraseñas no coinciden.");
                statusLabel.setTextFill(Color.RED);
                return;
            }

            String names = fieldNames.getText();
            String surname = fieldSurnames.getText();
            String selectedRoleText = roleBox.getValue();
            Role role = getRoleFromText(selectedRoleText);
            String userName = fieldUser.getText();
            String hashedPassword = PasswordHasher.hashPassword(password);

            UserDTO academic = new UserDTO("0", numberOffStaff, names, surname, userName, hashedPassword, role);

            try {
                boolean success = userService.registerUser(academic);

                statusLabel.setText("¡Académico registrado exitosamente!");
                statusLabel.setTextFill(Color.GREEN);

                if (parentController != null) {
                    parentController.loadAcademicData();
                }

                clearFields();

            } catch (RepeatedId e) {
                statusLabel.setText(e.getMessage());
                statusLabel.setTextFill(Color.RED);
                logger.error("Error - ID repetido: {}", e.getMessage());
            } catch (RepeatedName e) {
                statusLabel.setText(e.getMessage());
                statusLabel.setTextFill(Color.RED);
                logger.error("Error - Nombre de usuario repetido: {}", e.getMessage());
            } catch (SQLException e) {
                statusLabel.setText("No se pudo conectar a la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error de SQL al registrar el académico: {}", e.getMessage(), e);
            }
        } catch (EmptyFields | InvalidData e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(Color.RED);
            logger.error("Error: {}", e.getMessage(), e);
        }
    }

    private void clearFields() {
        fieldNumberOffStaff.clear();
        fieldNames.clear();
        fieldSurnames.clear();
        fieldUser.clear();
        fieldPassword.clear();
        fieldConfirmPassword.clear();
        fieldPasswordVisible.clear();
        fieldConfirmPasswordVisible.clear();
        roleBox.setValue(null);
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