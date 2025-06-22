package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import logic.DAO.GroupDAO;
import logic.DTO.GroupDTO;
import logic.DTO.StudentDTO;
import logic.exceptions.*;
import logic.services.StudentService;
import logic.validators.StudentValidator;
import logic.utils.PasswordHasher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class GUI_RegisterStudentController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_RegisterStudentController.class);

    private static final int MAX_NAMES = 50;
    private static final int MAX_SURNAMES = 50;
    private static final int MAX_PHONE = 30;
    private static final int MAX_EMAIL = 100;
    private static final int MAX_USER = 50;
    private static final int MAX_PASSWORD = 64;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField tuitionField, namesField, surnamesField, phoneField, emailField, userField, passwordVisibleField, confirmPasswordVisibleField, creditAdvanceField;

    @FXML
    private ChoiceBox<String> nrcChoiceBox;

    @FXML
    private PasswordField passwordField, confirmPasswordField;

    @FXML
    private Button togglePasswordVisibilityButton;

    @FXML
    private Label namesCharCountLabel, surnamesCharCountLabel, phoneCharCountLabel, emailCharCountLabel, userCharCountLabel, passwordCharCountLabel;

    private boolean isPasswordVisible = false;

    private GUI_CheckListOfStudentsController parentController;

    @FXML
    public void initialize() {
        configurePasswordVisibility();
        configureTextFormatters();
        configureCharCountLabels();
        loadNRCs();
    }

    private void configurePasswordVisibility() {
        togglePasswordVisibilityButton.setText("🙈");
        togglePasswordVisibilityButton.setOnAction(event -> togglePasswordVisibility());
    }

    private void configureTextFormatters() {
        namesField.setTextFormatter(createTextFormatter(MAX_NAMES));
        surnamesField.setTextFormatter(createTextFormatter(MAX_SURNAMES));
        phoneField.setTextFormatter(createTextFormatter(MAX_PHONE));
        emailField.setTextFormatter(createTextFormatter(MAX_EMAIL));
        userField.setTextFormatter(createTextFormatter(MAX_USER));
        passwordField.setTextFormatter(createTextFormatter(MAX_PASSWORD));
        creditAdvanceField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("\\d*") ? change : null
        ));
    }

    private TextFormatter<String> createTextFormatter(int maxLength) {
        return new TextFormatter<>(change ->
                change.getControlNewText().length() <= maxLength ? change : null
        );
    }

    private void configureCharCountLabels() {
        configureCharCount(namesField, namesCharCountLabel, MAX_NAMES);
        configureCharCount(surnamesField, surnamesCharCountLabel, MAX_SURNAMES);
        configureCharCount(phoneField, phoneCharCountLabel, MAX_PHONE);
        configureCharCount(emailField, emailCharCountLabel, MAX_EMAIL);
        configureCharCount(userField, userCharCountLabel, MAX_USER);
        configureCharCount(passwordField, passwordCharCountLabel, MAX_PASSWORD);
    }

    private void configureCharCount(TextField textField, Label charCountLabel, int maxLength) {
        charCountLabel.setText("0/" + maxLength);
        textField.textProperty().addListener((observable, oldText, newText) ->
                charCountLabel.setText(newText.length() + "/" + maxLength)
        );
    }

    public void setParentController(GUI_CheckListOfStudentsController parentController) {
        this.parentController = parentController;
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
    private void handleRegisterStudent() {
        statusLabel.setText("");
        statusLabel.setTextFill(Color.RED);

        if (!validateFields()) {
            return;
        }

        StudentDTO student = buildStudentDTO();
        if (student == null) {
            return;
        }

        try {
            registerStudent(student);
            statusLabel.setText("¡Estudiante registrado exitosamente!");
            statusLabel.setTextFill(Color.GREEN);
            if (parentController != null) {
                parentController.loadStudentData();
            }
        } catch (RepeatedTuition | RepeatedPhone | RepeatedEmail e) {
            statusLabel.setText(e.getMessage());
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
            } else if ("42000" .equals(sqlState)) {
                statusLabel.setText("Base de datos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos no encontrada: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado: {}", e);
            statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
        }
    }

    private boolean validateFields() {
        if (!areFieldsFilled()) {
            statusLabel.setText("Todos los campos deben estar llenos.");
            return false;
        }
        String tuition = tuitionField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String password = isPasswordVisible ? passwordVisibleField.getText() : passwordField.getText();
        try {
            StudentValidator.validateStudentData(tuition, email, phone, password);
        } catch (InvalidData e) {
            statusLabel.setText(e.getMessage());
            return false;
        }
        String confirmPassword = isPasswordVisible ? confirmPasswordVisibleField.getText() : confirmPasswordField.getText();
        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Las contraseñas no coinciden.");
            return false;
        }
        return true;
    }

    private StudentDTO buildStudentDTO() {
        StudentDTO studentDTO = new StudentDTO("N/A", 1, "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "0.0", 0.0);
        try {
            String tuition = tuitionField.getText();
            String password = isPasswordVisible ? passwordVisibleField.getText() : passwordField.getText();
            studentDTO = new StudentDTO(
                    tuition, 1, namesField.getText(), surnamesField.getText(), phoneField.getText(), emailField.getText(),
                    userField.getText(), PasswordHasher.hashPassword(password), nrcChoiceBox.getValue(), creditAdvanceField.getText(), 0.0
            );
        } catch (Exception e) {
            statusLabel.setText("Error al construir los datos del estudiante.");
            LOGGER.error("Error al construir StudentDTO: {}", e.getMessage(), e);
        }
        return studentDTO;
    }

    private void registerStudent(StudentDTO student) throws SQLException, RepeatedTuition, RepeatedPhone, RepeatedEmail {
        StudentService studentService = new StudentService();
        studentService.registerStudent(student);
    }

    private void loadNRCs() {
        try {
            GroupDAO groupDAO = new GroupDAO();
            List<GroupDTO> groups = groupDAO.getAllGroups();
            for (GroupDTO group : groups) {
                nrcChoiceBox.getItems().add(group.getNRC());
            }
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
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos no encontrada: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar los NRCs: {}", e.getMessage(), e);
        }
    }

    private boolean areFieldsFilled() {
        return !tuitionField.getText().isEmpty() &&
                !namesField.getText().isEmpty() &&
                !surnamesField.getText().isEmpty() &&
                !phoneField.getText().isEmpty() &&
                !emailField.getText().isEmpty() &&
                !userField.getText().isEmpty() &&
                (!passwordField.getText().isEmpty() || !passwordVisibleField.getText().isEmpty()) &&
                (!confirmPasswordField.getText().isEmpty() || !confirmPasswordVisibleField.getText().isEmpty()) &&
                nrcChoiceBox.getValue() != null &&
                !creditAdvanceField.getText().isEmpty();
    }
}