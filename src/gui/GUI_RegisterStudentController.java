package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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

    private static final Logger logger = LogManager.getLogger(GUI_RegisterStudentController.class);

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
    private Button togglePasswordVisibility;

    @FXML
    private Label namesCharCountLabel, surnamesCharCountLabel, phoneCharCountLabel, emailCharCountLabel, userCharCountLabel, passwordCharCountLabel;

    private boolean isPasswordVisible = false;

    private GUI_CheckListOfStudentsController parentController;

    @FXML
    public void initialize() {
        togglePasswordVisibility.setText("🙈");
        togglePasswordVisibility.setOnAction(event -> togglePasswordVisibility());
        loadNRCs();

        namesField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= MAX_NAMES ? change : null
        ));
        namesCharCountLabel.setText("0/" + MAX_NAMES);
        namesField.textProperty().addListener((observable, oldText, newText) ->
                namesCharCountLabel.setText(newText.length() + "/" + MAX_NAMES)
        );
        surnamesField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= MAX_SURNAMES ? change : null
        ));
        surnamesCharCountLabel.setText("0/" + MAX_SURNAMES);
        surnamesField.textProperty().addListener((observable, oldText, newText) ->
                surnamesCharCountLabel.setText(newText.length() + "/" + MAX_SURNAMES)
        );
        phoneField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= MAX_PHONE ? change : null
        ));
        phoneCharCountLabel.setText("0/" + MAX_PHONE);
        phoneField.textProperty().addListener((observable, oldText, newText) ->
                phoneCharCountLabel.setText(newText.length() + "/" + MAX_PHONE)
        );
        emailField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= MAX_EMAIL ? change : null
        ));
        emailCharCountLabel.setText("0/" + MAX_EMAIL);
        emailField.textProperty().addListener((observable, oldText, newText) ->
                emailCharCountLabel.setText(newText.length() + "/" + MAX_EMAIL)
        );
        userField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= MAX_USER ? change : null
        ));
        userCharCountLabel.setText("0/" + MAX_USER);
        userField.textProperty().addListener((observable, oldText, newText) ->
                userCharCountLabel.setText(newText.length() + "/" + MAX_USER)
        );
        passwordField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= MAX_PASSWORD ? change : null
        ));
        passwordCharCountLabel.setText("0/" + MAX_PASSWORD);
        passwordField.textProperty().addListener((observable, oldText, newText) ->
                passwordCharCountLabel.setText(newText.length() + "/" + MAX_PASSWORD)
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
            togglePasswordVisibility.setText("🙈");
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
            togglePasswordVisibility.setText("👁");
        }
        isPasswordVisible = !isPasswordVisible;
    }

    @FXML
    private void handleRegisterStudent() {
        statusLabel.setText("");
        statusLabel.setTextFill(javafx.scene.paint.Color.RED);

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
            statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            if (parentController != null) {
                parentController.loadStudentData();
            }
        } catch (RepeatedTuition | RepeatedPhone | RepeatedEmail e) {
            statusLabel.setText(e.getMessage());
        } catch (SQLException e) {
            logger.warn("Error al registrar el estudiante: {}", e.getMessage(), e);
            statusLabel.setText("Error de base de datos. Intente más tarde.");
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
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
        try {
            String tuition = tuitionField.getText();
            String password = isPasswordVisible ? passwordVisibleField.getText() : passwordField.getText();
            return new StudentDTO(
                    tuition, 1, namesField.getText(), surnamesField.getText(), phoneField.getText(), emailField.getText(),
                    userField.getText(), PasswordHasher.hashPassword(password), nrcChoiceBox.getValue(), creditAdvanceField.getText(), 0.0
            );
        } catch (Exception e) {
            statusLabel.setText("Error al construir los datos del estudiante.");
            logger.error("Error al construir StudentDTO: {}", e.getMessage(), e);
            return null;
        }
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
            logger.error("Error al cargar los NRCs: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al cargar los NRCs: {}", e.getMessage(), e);
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