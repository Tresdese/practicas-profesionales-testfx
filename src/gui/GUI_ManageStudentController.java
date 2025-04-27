package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DTO.StudentDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GUI_ManageStudentController {

    private static final Logger logger = LogManager.getLogger(GUI_ManageStudentController.class);

    @FXML
    private TextField fieldNames, fieldSurnames, fieldNRC, fieldCreditAdvance;

    @FXML
    private Label statusLabel;

    private StudentDTO student;

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

            statusLabel.setText("¡Estudiante actualizado exitosamente!");
            statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
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