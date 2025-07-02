package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import logic.DTO.Role;
import logic.DTO.UserDTO;
import logic.exceptions.*;
import logic.services.ServiceConfig;
import logic.services.UserService;
import logic.utils.StaffNumberValidator;
import logic.utils.PasswordHasher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GUI_RegisterAcademicController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_RegisterAcademicController.class);

    private static final int MAX_NUMBER_OFF_STAFF = 5;
    private static final int MAX_NAMES = 50;
    private static final int MAX_SURNAMES = 50;
    private static final int MAX_USER = 50;
    private static final int MAX_PASSWORD = 64;

    @FXML
    private ChoiceBox<Role> roleBox;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField numberOfStaffField, namesField, surnamesField, userField, passwordVisibleField, confirmPasswordVisibleField;

    @FXML
    private PasswordField passwordField, confirmPasswordField;

    @FXML
    private Button togglePasswordVisibilityButton;

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
        togglePasswordVisibilityButton.setText("🙈");
        togglePasswordVisibilityButton.setOnAction(event -> togglePasswordVisibility());

        setRoles();

        configureTextFormatters();
        configureCharCountLabels();

        try {
            ServiceConfig serviceConfig = new ServiceConfig();
            userService = serviceConfig.getUserService();
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {} ", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {} ", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {} ", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {} ", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                statusLabel.setText("Tabla o vista no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla o vista no encontrada: {} ", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                statusLabel.setText("Columna no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada: {} ", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("HY000")) {
                statusLabel.setText("Error general de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos: {} ", e.getMessage(), e);
            } else {
                statusLabel.setText("Error de base de datos al conectar a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de base de datos al conectar a la base de datos: {} ", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al leer la configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al leer la configuración de la base de datos: {} ", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al inicializar el servicio de usuario.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al inicializar el servicio de usuario: {} ", e.getMessage(), e);
        }
    }

    private void configureTextFormatters() {
        numberOfStaffField.setTextFormatter(createTextFormatter(MAX_NUMBER_OFF_STAFF));
        namesField.setTextFormatter(createTextFormatter(MAX_NAMES));
        surnamesField.setTextFormatter(createTextFormatter(MAX_SURNAMES));
        userField.setTextFormatter(createTextFormatter(MAX_USER));
        passwordField.setTextFormatter(createTextFormatter(MAX_PASSWORD));
        passwordVisibleField.setTextFormatter(createTextFormatter(MAX_PASSWORD));
        confirmPasswordField.setTextFormatter(createTextFormatter(MAX_PASSWORD));
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

    public void setRoles() {
        // Solo roles visibles (sin GUEST)
        List<Role> visibleRoles = Arrays.stream(Role.values())
                .filter(role -> role != Role.GUEST)
                .collect(Collectors.toList());
        roleBox.getItems().setAll(visibleRoles);
        roleBox.setConverter(new StringConverter<Role>() {
            @Override
            public String toString(Role role) {
                return role != null ? role.getDisplayName() : "";
            }
            @Override
            public Role fromString(String string) {
                for (Role role : visibleRoles) {
                    if (role.getDisplayName().equals(string)) {
                        return role;
                    }
                }
                statusLabel.setText("Rol no válido");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Rol no válido: {}", string);
                return null;
            }
        });
    }

    @FXML
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordField.setText(passwordVisibleField.getText());
            confirmPasswordField.setText(confirmPasswordVisibleField.getText());

            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
            confirmPasswordVisibleField.setVisible(false);
            confirmPasswordVisibleField.setManaged(false);

            passwordField.setVisible(true);
            passwordField.setManaged(true);
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);

            togglePasswordVisibilityButton.setText("🙈");
        } else {
            passwordVisibleField.setText(passwordField.getText());
            confirmPasswordVisibleField.setText(confirmPasswordField.getText());

            passwordField.setVisible(false);
            passwordField.setManaged(false);
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);

            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
            confirmPasswordVisibleField.setVisible(true);
            confirmPasswordVisibleField.setManaged(true);

            togglePasswordVisibilityButton.setText("👁");
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
            String confirmPassword = isPasswordVisible ? confirmPasswordVisibleField.getText() : confirmPasswordField.getText();

            if (!password.equals(confirmPassword)) {
                statusLabel.setText("Las contraseñas no coinciden.");
                statusLabel.setTextFill(Color.RED);
                return;
            }

            String names = namesField.getText();
            String surname = surnamesField.getText();
            Role role = roleBox.getValue();
            String userName = userField.getText();
            String hashedPassword = PasswordHasher.hashPassword(password);

            UserDTO academic = new UserDTO("0", 1, numberOffStaff, names, surname, userName, hashedPassword, role);

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
                LOGGER.error("Error - ID repetido: {}", e.getMessage());
            } catch (RepeatedName e) {
                statusLabel.setText(e.getMessage());
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error - Nombre de usuario repetido: {}", e.getMessage());
            } catch (SQLException e) {
                String sqlState = e.getSQLState();
                if ("08001".equals(sqlState)) {
                    statusLabel.setText("Error de conexión con la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                } else if ("08S01".equals(sqlState)) {
                    statusLabel.setText("Conexión interrumpida con la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                } else if ("42S02".equals(sqlState)) {
                    statusLabel.setText("Tabla o vista no encontrada.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Tabla o vista no encontrada: {}", e.getMessage(), e);
                } else if ("42S22".equals(sqlState)) {
                    statusLabel.setText("Columna no encontrada.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Columna no encontrada: {}", e.getMessage(), e);
                } else if ("HY000".equals(sqlState)) {
                    statusLabel.setText("Error general de la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
                } else if ("42000".equals(sqlState)) {
                    statusLabel.setText("Base de datos desconocida.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                } else if ("23000".equals(sqlState)) {
                    statusLabel.setText("Violación de restricción de integridad.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
                } else if ("28000".equals(sqlState)) {
                    statusLabel.setText("Acceso denegado a la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                } else {
                    statusLabel.setText("Error de base de datos al registrar académico.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Error de base de datos al registrar académico: {}", e.getMessage(), e);
                }
            }
        } catch (EmptyFields | InvalidData e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error: {}", e.getMessage(), e);
        } catch (IOException e) {
            statusLabel.setText("Error al leer la configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al leer la configuración de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al registrar académico: {}", e.getMessage(), e);
        }
    }

    private void clearFields() {
        numberOfStaffField.clear();
        namesField.clear();
        surnamesField.clear();
        userField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        passwordVisibleField.clear();
        confirmPasswordVisibleField.clear();
        roleBox.setValue(null);
    }

    public boolean areFieldsFilled() {
        return !numberOfStaffField.getText().trim().isEmpty() &&
                !namesField.getText().trim().isEmpty() &&
                !surnamesField.getText().trim().isEmpty() &&
                !userField.getText().trim().isEmpty() &&
                (!passwordField.getText().trim().isEmpty() || !passwordVisibleField.getText().trim().isEmpty()) &&
                (!confirmPasswordField.getText().trim().isEmpty() || !confirmPasswordVisibleField.getText().trim().isEmpty()) &&
                roleBox.getValue() != null;
    }

}