package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
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
        configureTextFormatters();
        configureCharCountLabels();
    }

    private void configureTextFormatters() {
        fieldNames.setTextFormatter(createTextFormatter(MAX_NAMES));
        surnamesField.setTextFormatter(createTextFormatter(MAX_SURNAMES));
    }

    private TextFormatter<String> createTextFormatter(int maxLength) {
        return new TextFormatter<>(change ->
                change.getControlNewText().length() <= maxLength ? change : null
        );
    }

    private void configureCharCountLabels() {
        configureCharCount(fieldNames, namesCharCountLabel, MAX_NAMES);
        configureCharCount(surnamesField, surnamesCharCountLabel, MAX_SURNAMES);
    }

    private void configureCharCount(TextField textField, Label charCountLabel, int maxLength) {
        charCountLabel.setText("0/" + maxLength);
        textField.textProperty().addListener((observable, oldText, newText) ->
                charCountLabel.setText(newText.length() + "/" + maxLength)
        );
    }


    @FXML
    private void handleUpdateProfile() {
        if (studentService == null) {
            logger.error("El servicio StudentService no ha sido inicializado.");
            statusLabel.setText("Error interno: Servicio no disponible.");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        if (currentStudent == null) {
            logger.error("El objeto currentStudent no ha sido inicializado.");
            statusLabel.setText("Error: No se pudo cargar la información del estudiante.");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        if (!areFieldsFilled()) {
            statusLabel.setText("Todos los campos deben estar llenos.");
            statusLabel.setTextFill(Color.RED);
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
            statusLabel.setTextFill(Color.GREEN);
        } catch (SQLException | RepeatedEmail | RepeatedPhone e) {
            logger.warn("Error al actualizar el perfil: {}", e.getMessage(), e);
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(Color.RED);
        } catch (InvalidData e) {
            logger.warn("Error de validación: {}", e.getMessage(), e);
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
            statusLabel.setTextFill(Color.RED);
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