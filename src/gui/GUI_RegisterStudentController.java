package gui;

import data_access.ConecctionDataBase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DAO.StudentDAO;
import logic.DTO.StudentDTO;
import logic.exceptions.*;
import logic.utils.PasswordHasher;
import logic.utils.TuitonValidator;
import logic.utils.EmailValidator;
import logic.utils.PhoneValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class GUI_RegisterStudentController {

    private static final Logger logger = LogManager.getLogger(GUI_RegisterStudentController.class);

    @FXML
    private Label statusLabel;

    @FXML
    private TextField fieldTuiton, fieldNames, fieldSurnames, fieldPhone, fieldEmail, fieldUser, fieldPasswordVisible, fieldConfirmPasswordVisible, fieldNRC, fieldCreditAdvance;

    @FXML
    private PasswordField fieldPassword, fieldConfirmPassword;

    @FXML
    private Button togglePasswordVisibility;

    private boolean isPasswordVisible = false;

    private GUI_CheckListOfStudentsController parentController; // Referencia al controlador de la tabla

    @FXML
    public void initialize() {
        togglePasswordVisibility.setText("🙈");
        togglePasswordVisibility.setOnAction(event -> togglePasswordVisibility());
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
                throw new EmptyFields("Todos los campos deben estar llenos.");
            }

            // Validaciones de datos
            String tuiton = fieldTuiton.getText();
            TuitonValidator.validate(tuiton);

            String email = fieldEmail.getText();
            EmailValidator.validate(email);

            String phone = fieldPhone.getText();
            PhoneValidator.validate(phone);

            String password = isPasswordVisible ? fieldPasswordVisible.getText() : fieldPassword.getText();
            String confirmPassword = isPasswordVisible ? fieldConfirmPasswordVisible.getText() : fieldConfirmPassword.getText();

            if (!password.equals(confirmPassword)) {
                throw new PasswordDoesNotMatch("Las contraseñas no coinciden.");
            }

            StudentDTO student = new StudentDTO(
                    tuiton, 1, fieldNames.getText(), fieldSurnames.getText(), phone, email,
                    fieldUser.getText(), PasswordHasher.hashPassword(password), fieldNRC.getText(), fieldCreditAdvance.getText()
            );

            ConecctionDataBase connectionDB = new ConecctionDataBase();
            try (Connection connection = connectionDB.connectDB()) {
                StudentDAO studentDAO = new StudentDAO();

                if (studentDAO.isTuitonRegistered(tuiton, connection)) {
                    throw new RepeatedTuiton("La matrícula ya está registrada.");
                }

                if (studentDAO.isPhoneRegistered(phone, connection)) {
                    throw new RepeatedPhone("El número de teléfono ya está registrado.");
                }

                if (studentDAO.isEmailRegistered(email, connection)) {
                    throw new RepeatedEmail("El correo electrónico ya está registrado.");
                }

                boolean success = studentDAO.insertStudent(student, connection);

                if (success) {
                    statusLabel.setText("¡Estudiante registrado exitosamente!");
                    statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);

                    if (parentController != null) {
                        parentController.loadStudentData();
                    }
                } else {
                    statusLabel.setText("No se pudo registrar el estudiante.");
                    statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                }
            } catch (SQLException e) {
                logger.error("Error de SQL al registrar el estudiante: {}", e.getMessage(), e);
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
        return !fieldTuiton.getText().isEmpty() &&
                !fieldNames.getText().isEmpty() &&
                !fieldSurnames.getText().isEmpty() &&
                !fieldPhone.getText().isEmpty() &&
                !fieldEmail.getText().isEmpty() &&
                !fieldUser.getText().isEmpty() &&
                (!fieldPassword.getText().isEmpty() || !fieldPasswordVisible.getText().isEmpty()) &&
                (!fieldConfirmPassword.getText().isEmpty() || !fieldConfirmPasswordVisible.getText().isEmpty()) &&
                !fieldNRC.getText().isEmpty() &&
                !fieldCreditAdvance.getText().isEmpty();
    }
}