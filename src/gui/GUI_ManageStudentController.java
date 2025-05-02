package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DTO.StudentDTO;
import logic.services.ServiceFactory;
import logic.services.StudentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class GUI_ManageStudentController {

    private static final Logger logger = LogManager.getLogger(GUI_ManageStudentController.class);

    @FXML
    private TextField fieldNames, fieldSurnames, fieldNRC, fieldCreditAdvance;

    @FXML
    private Label statusLabel;

    private StudentDTO student;
    private StudentService studentService;

    @FXML
    private void initialize() {
        try {
            this.studentService = ServiceFactory.getStudentService();
        } catch (Exception e) {
            logger.error("Error al obtener StudentService desde ServiceFactory: {}", e.getMessage(), e);
            statusLabel.setText("Error al conectar con la base de datos.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    public void setStudentData(StudentDTO student) {
        if (student == null) {
            logger.error("El objeto StudentDTO es nulo.");
            return;
        }

        this.student = student;

        fieldNames.setText(student.getNames() != null ? student.getNames() : "");
        fieldSurnames.setText(student.getSurnames() != null ? student.getSurnames() : "");
        fieldNRC.setText(student.getNRC() != null ? student.getNRC() : "");
        fieldCreditAdvance.setText(student.getCreditAdvance() != null ? student.getCreditAdvance() : "");
    }

    @FXML
    private void handleSaveChanges() {
        if (studentService == null) {
            logger.error("StudentService no ha sido inicializado.");
            statusLabel.setText("Error interno. Intente más tarde.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        try {
            if (!areFieldsFilled()) {
                throw new IllegalArgumentException("Todos los campos deben estar llenos.");
            }

            String names = fieldNames.getText();
            String surnames = fieldSurnames.getText();
            String nrc = fieldNRC.getText();
            String creditAdvance = fieldCreditAdvance.getText();

            student.setNames(names);
            student.setSurnames(surnames);
            student.setNRC(nrc);
            student.setCreditAdvance(creditAdvance);

            studentService.updateStudent(student);

            statusLabel.setText("¡Estudiante actualizado exitosamente!");
            statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
        } catch (SQLException e) {
            statusLabel.setText("Error al actualizar en la base de datos.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            logger.error("Error al actualizar el estudiante en la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            logger.error("Error: {}", e.getMessage(), e);
        }
    }

    private boolean areFieldsFilled() {
        return !fieldNames.getText().isEmpty() &&
                !fieldSurnames.getText().isEmpty() &&
                !fieldNRC.getText().isEmpty() &&
                !fieldCreditAdvance.getText().isEmpty();
    }
}