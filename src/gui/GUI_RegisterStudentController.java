package gui;

import data_access.ConecctionDataBase;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import logic.DAO.StudentDAO;
import logic.DTO.StudentDTO;
import logic.exceptions.*;
import logic.utils.EmailValidator;
import logic.utils.PhoneValidator;
import logic.utils.TuitonValidator;
import logic.utils.PasswordHasher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class GUI_RegisterStudentController {

    private static final Logger logger = LogManager.getLogger(GUI_RegisterStudentController.class);

    @FXML
    private Label label;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField fieldTuiton, fieldNames, fieldSurnames, fieldPhone, fieldEmail, fieldUser, fieldPassword, fieldNRC, fieldCreditAdvance;

    @FXML
    private Button buttonRegisterStudent;

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleRegisterStudent() {
        try {
            if (!areFieldsFilled()) {
                throw new EmptyFields("Todos los campos deben estar llenos.");
            }

            String tuiton = fieldTuiton.getText();
            TuitonValidator.validate(tuiton);

            String email = fieldEmail.getText();
            EmailValidator.validate(email);

            String phone = fieldPhone.getText();
            PhoneValidator.validate(phone);

            String names = fieldNames.getText();
            String surnames = fieldSurnames.getText();
            String user = fieldUser.getText();
            String password = fieldPassword.getText();
            String nrc = fieldNRC.getText();
            String creditAdvance = fieldCreditAdvance.getText();

            String hashedPassword = PasswordHasher.hashPassword(password);

            StudentDTO student = new StudentDTO(tuiton, 1, names, surnames, phone, email, user, hashedPassword, nrc, creditAdvance);

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
                } else {
                    statusLabel.setText("El estudiante ya existe.");
                    statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                }
            } catch (SQLException e) {
                statusLabel.setText("No se pudo conectar a la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                logger.error("Error de SQL al registrar el estudiante: {}", e.getMessage(), e);
            } finally {
                connectionDB.closeConnection();
            }
        } catch (EmptyFields | InvalidData | RepeatedTuiton | RepeatedPhone | RepeatedEmail e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            logger.error("Error: {}", e.getMessage(), e);
        }
    }

    public boolean areFieldsFilled() {
        return !fieldTuiton.getText().isEmpty() &&
                !fieldNames.getText().isEmpty() &&
                !fieldSurnames.getText().isEmpty() &&
                !fieldPhone.getText().isEmpty() &&
                !fieldEmail.getText().isEmpty() &&
                !fieldUser.getText().isEmpty() &&
                !fieldPassword.getText().isEmpty() &&
                !fieldNRC.getText().isEmpty() &&
                !fieldCreditAdvance.getText().isEmpty();
    }
}