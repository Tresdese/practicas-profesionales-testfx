package gui;

import data_access.ConecctionDataBase;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import logic.DAO.StudentDAO;
import logic.DTO.StudentDTO;
import logic.exceptions.RepeatedTuiton;
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
        // Se eliminó el mensaje de inicialización
    }

    @FXML
    private void handleRegisterStudent() {
        String tuiton = fieldTuiton.getText();
        String names = fieldNames.getText();
        String surnames = fieldSurnames.getText();
        String phone = fieldPhone.getText();
        String email = fieldEmail.getText();
        String user = fieldUser.getText();
        String password = fieldPassword.getText();
        String nrc = fieldNRC.getText();
        String creditAdvance = fieldCreditAdvance.getText();

        // Cifrar la contraseña
        String hashedPassword = PasswordHasher.hashPassword(password);

        StudentDTO student = new StudentDTO(tuiton, 1, names, surnames, phone, email, user, hashedPassword, nrc, creditAdvance);

        ConecctionDataBase connectionDB = new ConecctionDataBase();
        try (Connection connection = connectionDB.connectDB()) {
            StudentDAO studentDAO = new StudentDAO();
            boolean success = studentDAO.insertStudent(student, connection);

            if (success) {
                statusLabel.setText("¡Estudiante registrado exitosamente!");
                statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            } else {
                statusLabel.setText("El estudiante ya existe o ocurrió un error.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            }
        } catch (RepeatedTuiton e) {
            // Mostrar el mensaje de la excepción en el label
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);

            // Registrar la excepción en el log
            logger.error("Error al registrar el estudiante: {}", e.getMessage(), e);
        } catch (SQLException e) {
            statusLabel.setText("Error al registrar el estudiante: " + e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);

            // Registrar la excepción en el log
            logger.error("Error de SQL al registrar el estudiante: {}", e.getMessage(), e);
        } finally {
            connectionDB.closeConnection();
        }
    }
}