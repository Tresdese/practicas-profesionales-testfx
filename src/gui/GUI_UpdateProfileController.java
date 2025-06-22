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

    private static final Logger LOGGER = LogManager.getLogger(GUI_UpdateProfileController.class);

    private static final int MAX_NAMES = 50;
    private static final int MAX_SURNAMES = 50;

    @FXML
    private TextField namesField, surnamesField, phoneField, emailField;

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
        namesField.setTextFormatter(createTextFormatter(MAX_NAMES));
        surnamesField.setTextFormatter(createTextFormatter(MAX_SURNAMES));
    }

    private TextFormatter<String> createTextFormatter(int maxLength) {
        return new TextFormatter<>(change ->
                change.getControlNewText().length() <= maxLength ? change : null
        );
    }

    private void configureCharCountLabels() {
        configureCharCount(namesField, namesCharCountLabel, MAX_NAMES);
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
            LOGGER.error("El servicio StudentService no ha sido inicializado.");
            statusLabel.setText("Error interno: Servicio no disponible.");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        if (currentStudent == null) {
            LOGGER.error("El objeto currentStudent no ha sido inicializado.");
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
                    namesField.getText(),
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
        } catch (RepeatedEmail e) {
        LOGGER.warn("Correo repetido: {}", e);
        statusLabel.setText("El correo ingresado ya está registrado.");
        statusLabel.setTextFill(Color.RED);
    } catch (RepeatedPhone e) {
        LOGGER.warn("Teléfono repetido: {}", e);
        statusLabel.setText("El teléfono ingresado ya está registrado.");
        statusLabel.setTextFill(Color.RED);
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
            } else {
                statusLabel.setText("Error al actualizar el perfil.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al actualizar el perfil: {}", e.getMessage(), e);
            }
    } catch (InvalidData e) {
        LOGGER.warn("Error de validación: {}", e.getMessage(), e);
        statusLabel.setText(e.getMessage());
        statusLabel.setTextFill(Color.RED);
    } catch (Exception e) {
        LOGGER.error("Error inesperado: {}", e);
        statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
        statusLabel.setTextFill(Color.RED);
        }
    }

    private boolean areFieldsFilled() {
        return !namesField.getText().isEmpty() &&
                !surnamesField.getText().isEmpty() &&
                !phoneField.getText().isEmpty() &&
                !emailField.getText().isEmpty();
    }

    public void setStudentData(String names, String surnames, String phone, String email) {
        namesField.setText(names);
        surnamesField.setText(surnames);
        phoneField.setText(phone);
        emailField.setText(email);
    }

    public void setCurrentStudent(StudentDTO student) {
        this.currentStudent = student;
    }
}