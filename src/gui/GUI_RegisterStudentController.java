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
            // Validar si algún campo está vacío
            if (fieldTuiton.getText().isEmpty() || fieldNames.getText().isEmpty() || fieldSurnames.getText().isEmpty() ||
                    fieldPhone.getText().isEmpty() || fieldEmail.getText().isEmpty() || fieldUser.getText().isEmpty() ||
                    fieldPassword.getText().isEmpty() || fieldNRC.getText().isEmpty() || fieldCreditAdvance.getText().isEmpty()) {
                throw new EmptyFields("Todos los campos deben estar llenos.");
            }

            // Validar matrícula
            String tuiton = fieldTuiton.getText();
            TuitonValidator.validate(tuiton);

            // Validar correo electrónico
            String email = fieldEmail.getText();
            EmailValidator.validate(email);

            // Validar número de teléfono
            String phone = fieldPhone.getText();
            PhoneValidator.validate(phone);

            // Obtener los demás valores de los campos
            String names = fieldNames.getText();
            String surnames = fieldSurnames.getText();
            String user = fieldUser.getText();
            String password = fieldPassword.getText();
            String nrc = fieldNRC.getText();
            String creditAdvance = fieldCreditAdvance.getText();

            // Hashear la contraseña
            String hashedPassword = PasswordHasher.hashPassword(password);

            // Crear el objeto StudentDTO
            StudentDTO student = new StudentDTO(tuiton, 1, names, surnames, phone, email, user, hashedPassword, nrc, creditAdvance);

            // Conectar a la base de datos y registrar al estudiante
            ConecctionDataBase connectionDB = new ConecctionDataBase();
            try (Connection connection = connectionDB.connectDB()) {
                StudentDAO studentDAO = new StudentDAO();

                // Verificar si la matrícula ya está registrada
                if (studentDAO.isTuitonRegistered(tuiton, connection)) {
                    throw new RepeatedTuiton("La matrícula ya está registrada.");
                }

                // Verificar si el número de teléfono ya está registrado
                if (studentDAO.isPhoneRegistered(phone, connection)) {
                    throw new RepeatedPhone("El número de teléfono ya está registrado.");
                }

                // Verificar si el correo ya está registrado
                if (studentDAO.isEmailRegistered(email, connection)) {
                    throw new RepeatedEmail("El correo electrónico ya está registrado.");
                }

                // Insertar al estudiante
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
}