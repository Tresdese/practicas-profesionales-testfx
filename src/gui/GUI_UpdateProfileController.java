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

    private static final int MAX_NAMES = 50;
    private static final int MAX_SURNAMES = 50;

    @FXML
    private TextField fieldNames, surnamesField, phoneField, emailField;

    @FXML
    private Label statusLabel, namesCharCountLabel, surnamesCharCountLabel;

    private StudentDTO currentStudent;
    private StudentService studentService;

    public void setStudentService(StudentService studentService) {
        this.studentService = studentService;
    }

    @FXML
    public void initialize() {
        fieldNames.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= MAX_NAMES ? change : null
        ));
        namesCharCountLabel.setText("0/" + MAX_NAMES);
        fieldNames.textProperty().addListener((obs, oldText, newText) ->
                namesCharCountLabel.setText(newText.length() + "/" + MAX_NAMES)
        );

        surnamesField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= MAX_SURNAMES ? change : null
        ));
        surnamesCharCountLabel.setText("0/" + MAX_SURNAMES);
        surnamesField.textProperty().addListener((obs, oldText, newText) ->
                surnamesCharCountLabel.setText(newText.length() + "/" + MAX_SURNAMES)
        );
    }


    @FXML
    private void handleUpdateProfile() {
        if (studentService == null) {
            logger.error("El servicio StudentService no ha sido inicializado.");
            statusLabel.setText("Error interno: Servicio no disponible.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

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
            String email = emailField.getText();
            String phone = phoneField.getText();
            StudentValidator.validateStudentData(email, phone);

            StudentDTO updatedStudent = new StudentDTO(
                    currentStudent.getTuition(),
                    currentStudent.getState(),
                    fieldNames.getText(),
                    surnamesField.getText(),
                    phone,
                    email,
                    currentStudent.getUser(),
                    currentStudent.getPassword(),
                    currentStudent.getNRC(),
                    currentStudent.getCreditAdvance(),
                    currentStudent.getFinalGrade()
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
                !surnamesField.getText().isEmpty() &&
                !phoneField.getText().isEmpty() &&
                !emailField.getText().isEmpty();
    }

    public void setStudentData(String names, String surnames, String phone, String email) {
        fieldNames.setText(names);
        surnamesField.setText(surnames);
        phoneField.setText(phone);
        emailField.setText(email);
    }

    public void setCurrentStudent(StudentDTO student) {
        this.currentStudent = student;
    }
}