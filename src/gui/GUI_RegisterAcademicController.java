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

    private static final int MAX_NUMBER_OFF_STAFF = 5;
    private static final int MAX_NAMES = 50;
    private static final int MAX_SURNAMES = 50;
    private static final int MAX_USER = 50;
    private static final int MAX_PASSWORD = 64;

    @FXML
    private ChoiceBox<String> roleBox;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField numberOfStaffField, namesField, surnamesField, userField, passwordVisibleField, confirmPasswordVisibleField;

    @FXML
    private PasswordField passwordField, fieldConfirmPassword;

    @FXML
    private Button togglePasswordVisibility;

    @FXML
    private Label numberOffStaffCharCountLabel, namesCharCountLabel, surnamesCharCountLabel, userCharCountLabel, passwordCharCountLabel;

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

        configureTextFormatters();
        configureCharCountLabels();

        try {
            ServiceConfig serviceConfig = new ServiceConfig();
            userService = serviceConfig.getUserService();
        } catch (SQLException e) {
            statusLabel.setText("Error al conectar a la base de datos.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error al inicializar el servicio de usuarios: {}", e.getMessage(), e);
        }
    }

    private void configureTextFormatters() {
        numberOfStaffField.setTextFormatter(createTextFormatter(MAX_NUMBER_OFF_STAFF));
        namesField.setTextFormatter(createTextFormatter(MAX_NAMES));
        surnamesField.setTextFormatter(createTextFormatter(MAX_SURNAMES));
        userField.setTextFormatter(createTextFormatter(MAX_USER));
        passwordField.setTextFormatter(createTextFormatter(MAX_PASSWORD));
        passwordVisibleField.setTextFormatter(createTextFormatter(MAX_PASSWORD));
        fieldConfirmPassword.setTextFormatter(createTextFormatter(MAX_PASSWORD));
        confirmPasswordVisibleField.setTextFormatter(createTextFormatter(MAX_PASSWORD));
    }

    private TextFormatter<String> createTextFormatter(int maxLength) {
        return new TextFormatter<>(change ->
                change.getControlNewText().length() <= maxLength ? change : null
        );
    }

    private void configureCharCountLabels() {
        configureCharCount(numberOfStaffField, numberOffStaffCharCountLabel, MAX_NUMBER_OFF_STAFF);
        configureCharCount(namesField, namesCharCountLabel, MAX_NAMES);
        configureCharCount(surnamesField, surnamesCharCountLabel, MAX_SURNAMES);
        configureCharCount(userField, userCharCountLabel, MAX_USER);
        configureCharCount(passwordField, passwordCharCountLabel, MAX_PASSWORD);
        configureCharCount(passwordVisibleField, passwordCharCountLabel, MAX_PASSWORD);
    }

    private void configureCharCount(TextField textField, Label charCountLabel, int maxLength) {
        charCountLabel.setText("0/" + maxLength);
        textField.textProperty().addListener((observable, oldText, newText) ->
                charCountLabel.setText(newText.length() + "/" + maxLength)
        );
    }

    @FXML
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordField.setText(passwordVisibleField.getText());
            fieldConfirmPassword.setText(confirmPasswordVisibleField.getText());

            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
            confirmPasswordVisibleField.setVisible(false);
            confirmPasswordVisibleField.setManaged(false);

            passwordField.setVisible(true);
            passwordField.setManaged(true);
            fieldConfirmPassword.setVisible(true);
            fieldConfirmPassword.setManaged(true);

            togglePasswordVisibility.setText("🙈");
        } else {
            passwordVisibleField.setText(passwordField.getText());
            confirmPasswordVisibleField.setText(fieldConfirmPassword.getText());

            passwordField.setVisible(false);
            passwordField.setManaged(false);
            fieldConfirmPassword.setVisible(false);
            fieldConfirmPassword.setManaged(false);

            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
            confirmPasswordVisibleField.setVisible(true);
            confirmPasswordVisibleField.setManaged(true);

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
            String numberOffStaff = numberOfStaffField.getText();
            StaffNumberValidator.validate(numberOffStaff);

            String password = isPasswordVisible ? passwordVisibleField.getText() : passwordField.getText();
            String confirmPassword = isPasswordVisible ? confirmPasswordVisibleField.getText() : fieldConfirmPassword.getText();

            if (!password.equals(confirmPassword)) {
                statusLabel.setText("Las contraseñas no coinciden.");
                statusLabel.setTextFill(Color.RED);
                return;
            }

            String names = namesField.getText();
            String surname = surnamesField.getText();
            String selectedRoleText = roleBox.getValue();
            Role role = getRoleFromText(selectedRoleText);
            String userName = userField.getText();
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
        numberOfStaffField.clear();
        namesField.clear();
        surnamesField.clear();
        userField.clear();
        passwordField.clear();
        fieldConfirmPassword.clear();
        passwordVisibleField.clear();
        confirmPasswordVisibleField.clear();
        roleBox.setValue(null);
    }

    public boolean areFieldsFilled() {
        return !numberOfStaffField.getText().isEmpty() &&
                !namesField.getText().isEmpty() &&
                !surnamesField.getText().isEmpty() &&
                !userField.getText().isEmpty() &&
                (!passwordField.getText().isEmpty() || !passwordVisibleField.getText().isEmpty()) &&
                (!fieldConfirmPassword.getText().isEmpty() || !confirmPasswordVisibleField.getText().isEmpty()) &&
                roleBox.getValue() != null;
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