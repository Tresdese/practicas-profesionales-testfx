package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import logic.DTO.StudentDTO;
import logic.services.ServiceFactory;
import logic.services.StudentService;
import logic.utils.GradeValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class GUI_RecordFinalGradeController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_RecordFinalGradeController.class);

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
        } catch (IllegalArgumentException e) {
            LOGGER.error("Error al obtener StudentService desde ServiceFactory: {}", e.getMessage(), e);
            statusLabel.setText("Error de configuración del servicio.");
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            LOGGER.error("Error al obtener StudentService desde ServiceFactory: {}", e.getMessage(), e);
            statusLabel.setText("Error al conectar con la base de datos.");
            statusLabel.setTextFill(Color.RED);
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
            statusLabel.setTextFill(Color.GREEN);
        } catch (NumberFormatException e) {
            statusLabel.setText("Ingrese un número válido.");
            statusLabel.setTextFill(Color.RED);
        } catch (IllegalArgumentException e) {
            statusLabel.setText(e.getMessage());
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
            } else if ("42S02".equals(sqlState)) {
                statusLabel.setText("Tabla no encontrada en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if ("42S22".equals(sqlState)) {
                statusLabel.setText("Columna no encontrada en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else if ("HY000".equals(sqlState)) {
                statusLabel.setText("Error general de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al guardar la calificación final.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al guardar la calificación final: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al leer el archivo de configuracion de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al leer el archivo de configuración de la base de datos: {}", e.getMessage(), e);
        }
        catch (Exception e) {
            statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al guardar la calificación final: {}", e.getMessage(), e);
        }
    }
}