package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DTO.StudentDTO;
import logic.services.ServiceFactory;
import logic.services.StudentService;
import logic.utils.GradeValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class GUI_RecordFinalGradeController {

    private static final Logger logger = LogManager.getLogger(GUI_RecordFinalGradeController.class);

    @FXML
    private TextField finalGradeField;

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
        }
    }

    public void setStudent(StudentDTO student) {
        this.student = student;
        if (student != null) {
            finalGradeField.setText(String.valueOf(student.getFinalGrade()));
        }
    }

    @FXML
    private void handleSaveFinalGrade() {
        if (student == null || studentService == null) {
            statusLabel.setText("Error interno. Intente más tarde.");
            return;
        }

        String finalGradeText = finalGradeField.getText().trim();
        if (finalGradeText.isEmpty()) {
            statusLabel.setText("El campo de calificación no puede estar vacío.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        try {
            GradeValidator.validate(finalGradeText);
            double finalGrade = Double.parseDouble(finalGradeText);
            student.setFinalGrade(finalGrade);
            studentService.updateStudent(student);

            statusLabel.setText("¡Calificación final guardada exitosamente!");
            statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
        } catch (NumberFormatException e) {
            statusLabel.setText("Ingrese un número válido.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        } catch (IllegalArgumentException e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        } catch (SQLException e) {
            statusLabel.setText("Error al guardar la calificación.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            logger.error("Error al actualizar la calificación final: {}", e.getMessage(), e);
        }
    }
}