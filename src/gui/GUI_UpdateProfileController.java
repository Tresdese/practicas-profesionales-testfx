package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DTO.StudentDTO;
import logic.exceptions.*;
import logic.services.StudentService;
import logic.validators.StudentValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class GUI_UpdateProfileController {

    private static final Logger logger = LogManager.getLogger(GUI_UpdateProfileController.class);

    @FXML
    private TextField fieldNames, fieldSurnames, fieldPhone, fieldEmail;

    @FXML
    private Label statusLabel;

    private StudentDTO currentStudent;
    private StudentService studentService; // Inyección de dependencia

    public void setStudentService(StudentService studentService) {
        this.studentService = studentService;
    }

    @FXML
    private void handleUpdateProfile() {
        if (currentStudent == null) {
            logger.error("El objeto currentStudent no ha sido inicializado.");
            statusLabel.setText("Error: No se pudo cargar la información del estudiante.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        if (!areFieldsFilled()) {
            statusLabel.setText("Todos los campos deben estar llenos.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        try {
            String email = fieldEmail.getText();
            String phone = fieldPhone.getText();
            StudentValidator.validateStudentData(email, phone);

            StudentDTO updatedStudent = new StudentDTO(
                    currentStudent.getTuiton(),
                    currentStudent.getState(),
                    fieldNames.getText(),
                    fieldSurnames.getText(),
                    phone,
                    email,
                    currentStudent.getUser(),
                    currentStudent.getPassword(),
                    currentStudent.getNRC(),
                    currentStudent.getCreditAdvance(),
                    currentStudent.getCalificacionFinal()
            );

            studentService.updateStudent(updatedStudent);

            statusLabel.setText("¡Perfil actualizado exitosamente!");
            statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
        } catch (SQLException | RepeatedEmail | RepeatedPhone e) {
            logger.warn("Error al actualizar el perfil: {}", e.getMessage(), e);
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        } catch (InvalidData e) {
            logger.warn("Error de validación: {}", e.getMessage(), e);
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    private boolean areFieldsFilled() {
        return !fieldNames.getText().isEmpty() &&
                !fieldSurnames.getText().isEmpty() &&
                !fieldPhone.getText().isEmpty() &&
                !fieldEmail.getText().isEmpty();
    }

    public void setStudentData(String names, String surnames, String phone, String email) {
        fieldNames.setText(names);
        fieldSurnames.setText(surnames);
        fieldPhone.setText(phone);
        fieldEmail.setText(email);
    }

    public void setCurrentStudent(StudentDTO student) {
        this.currentStudent = student;
    }
}