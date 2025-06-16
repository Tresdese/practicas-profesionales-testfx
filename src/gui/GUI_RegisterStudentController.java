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

    private static final int MAX_NOMBRES = 50;

    private static final int MAX_APELLIDOS = 50;

    private static final int MAX_TELEFONO = 30;

    private static final int MAX_CORREO = 100;

    private static final int MAX_USUARIO = 50;

    private static final int MAX_PASSWORD = 64;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField fieldTuition, fieldNames, fieldSurnames, fieldPhone, fieldEmail, fieldUser, fieldPasswordVisible, fieldConfirmPasswordVisible, fieldCreditAdvance;

    @FXML
    private ChoiceBox<String> choiceBoxNRC;

    @FXML
    private PasswordField fieldPassword, fieldConfirmPassword;

    @FXML
    private Button togglePasswordVisibility;

    @FXML
    private Label labelNamesCharCount, labelSurnamesCharCount, labelPhoneCharCount, labelEmailCharCount, labelUserCharCount, labelPasswordCharCount;

    private boolean isPasswordVisible = false;

    private GUI_CheckListOfStudentsController parentController;

    @FXML
    public void initialize() {
        togglePasswordVisibility.setText("🙈");
        togglePasswordVisibility.setOnAction(event -> togglePasswordVisibility());
        loadNRCs();

        fieldNames.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= MAX_NOMBRES ? change : null
        ));
        labelNamesCharCount.setText("0/" + MAX_NOMBRES);
        fieldNames.textProperty().addListener((observable, oldText, newText) ->
                labelNamesCharCount.setText(newText.length() + "/" + MAX_NOMBRES)
        );
        fieldSurnames.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= MAX_APELLIDOS ? change : null
        ));
        labelSurnamesCharCount.setText("0/" + MAX_APELLIDOS);
        fieldSurnames.textProperty().addListener((observable, oldText, newText) ->
                labelSurnamesCharCount.setText(newText.length() + "/" + MAX_APELLIDOS)
        );
        fieldPhone.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= MAX_TELEFONO ? change : null
        ));
        labelPhoneCharCount.setText("0/" + MAX_TELEFONO);
        fieldPhone.textProperty().addListener((observable, oldText, newText) ->
                labelPhoneCharCount.setText(newText.length() + "/" + MAX_TELEFONO)
        );
        fieldEmail.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= MAX_CORREO ? change : null
        ));
        labelEmailCharCount.setText("0/" + MAX_CORREO);
        fieldEmail.textProperty().addListener((observable, oldText, newText) ->
                labelEmailCharCount.setText(newText.length() + "/" + MAX_CORREO)
        );
        fieldUser.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= MAX_USUARIO ? change : null
        ));
        labelUserCharCount.setText("0/" + MAX_USUARIO);
        fieldUser.textProperty().addListener((observable, oldText, newText) ->
                labelUserCharCount.setText(newText.length() + "/" + MAX_USUARIO)
        );
        fieldPassword.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= MAX_PASSWORD ? change : null
        ));
        labelPasswordCharCount.setText("0/" + MAX_PASSWORD);
        fieldPassword.textProperty().addListener((observable, oldText, newText) ->
                labelPasswordCharCount.setText(newText.length() + "/" + MAX_PASSWORD)
        );
    }

    public void setParentController(GUI_CheckListOfStudentsController parentController) {
        this.parentController = parentController;
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
    private void handleRegisterStudent() {
        try {
            if (!areFieldsFilled()) {
                statusLabel.setText("Todos los campos deben estar llenos.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                return;
            }
            String tuiton = fieldTuition.getText();
            String email = fieldEmail.getText();
            String phone = fieldPhone.getText();
            String password = fieldPassword.getText();
            StudentValidator.validateStudentData(tuiton, email, phone, password);

            password = isPasswordVisible ? fieldPasswordVisible.getText() : fieldPassword.getText();
            String confirmPassword = isPasswordVisible ? fieldConfirmPasswordVisible.getText() : fieldConfirmPassword.getText();

            if (!password.equals(confirmPassword)) {
                throw new PasswordDoesNotMatch("Las contraseñas no coinciden.");
            }

            StudentDTO student = new StudentDTO(
                    tuiton, 1, fieldNames.getText(), fieldSurnames.getText(), phone, email,
                    fieldUser.getText(), PasswordHasher.hashPassword(password), choiceBoxNRC.getValue(), fieldCreditAdvance.getText(), 0.0
            );

            try {
                StudentService studentService = new StudentService();
                studentService.registerStudent(student);

                statusLabel.setText("¡Estudiante registrado exitosamente!");
                statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);

                if (parentController != null) {
                    parentController.loadStudentData();
                }
            } catch (SQLException | RepeatedTuition | RepeatedPhone | RepeatedEmail e) {
                logger.warn("Error al registrar el estudiante: {}", e.getMessage(), e);
                statusLabel.setText(e.getMessage());
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            }
        } catch (EmptyFields | InvalidData | PasswordDoesNotMatch e) {
            logger.warn("Error de validación: {}", e.getMessage(), e);
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    private void loadNRCs() {
        try {
            GroupDAO groupDAO = new GroupDAO();
            List<GroupDTO> groups = groupDAO.getAllGroups();
            for (GroupDTO group : groups) {
                choiceBoxNRC.getItems().add(group.getNRC());
            }
        } catch (SQLException e) {
            logger.error("Error al cargar los NRCs: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al cargar los NRCs: {}", e.getMessage(), e);
        }
    }

    private boolean areFieldsFilled() {
        return !fieldTuition.getText().isEmpty() &&
                !fieldNames.getText().isEmpty() &&
                !fieldSurnames.getText().isEmpty() &&
                !fieldPhone.getText().isEmpty() &&
                !fieldEmail.getText().isEmpty() &&
                !fieldUser.getText().isEmpty() &&
                (!fieldPassword.getText().isEmpty() || !fieldPasswordVisible.getText().isEmpty()) &&
                (!fieldConfirmPassword.getText().isEmpty() || !fieldConfirmPasswordVisible.getText().isEmpty()) &&
                choiceBoxNRC.getValue() != null &&
                !fieldCreditAdvance.getText().isEmpty();
    }
}