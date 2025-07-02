package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logic.DTO.ProjectDTO;
import logic.DTO.StudentDTO;

import java.io.IOException;

public class GUI_ManageStudentController {

    @FXML
    private TextField namesField;
    @FXML
    private TextField surnamesField;
    @FXML
    private ChoiceBox<String> nrcChoiceBox;
    @FXML
    private TextField creditAdvanceField;
    @FXML
    private Button saveGradeButton;
    @FXML
    private Button assignFinalGradeButton;
    @FXML
    private Label statusLabel;
    @FXML
    private Label namesCharCountLabel;
    @FXML
    private Label surnamesCharCountLabel;

    private StudentDTO student;
    private ProjectDTO project;

    @FXML
    public void initialize() {

        namesField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (namesField.getText().length() >= 50) event.consume();
            namesCharCountLabel.setText(namesField.getText().length() + "/50");
        });
        surnamesField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (surnamesField.getText().length() >= 50) event.consume();
            surnamesCharCountLabel.setText(surnamesField.getText().length() + "/50");
        });

        namesField.textProperty().addListener((obs, oldValue, newValue) ->
                namesCharCountLabel.setText(newValue.length() + "/50"));
        surnamesField.textProperty().addListener((obs, oldValue, newValue) ->
                surnamesCharCountLabel.setText(newValue.length() + "/50"));

        saveGradeButton.setOnAction(event -> handleSaveChanges());
        assignFinalGradeButton.setOnAction(event -> handleAssignFinalGrade());
    }

    public void setStudentData(StudentDTO student, ProjectDTO project) {
        this.student = student;
        this.project = project;
        if (student != null) {
            namesField.setText(student.getNames());
            surnamesField.setText(student.getSurnames());
            nrcChoiceBox.setValue(student.getNRC());
            creditAdvanceField.setText(student.getCreditAdvance());
        }
    }

    private boolean validateFields() {
        boolean isValid = true;
        if (namesField.getText().isEmpty() ||
                surnamesField.getText().isEmpty() ||
                nrcChoiceBox.getValue() == null ||
                creditAdvanceField.getText().isEmpty()) {
            statusLabel.setText("Todos los campos deben estar llenos.");
            statusLabel.setTextFill(Color.RED);
            isValid = false;
        }
        return isValid;
    }

    public void handleSaveChanges() {
        if (!validateFields()) return;

        statusLabel.setText("¡Estudiante actualizado exitosamente!");
        statusLabel.setTextFill(Color.GREEN);

        if (student != null) {
            student.setNames(namesField.getText());
            student.setSurnames(surnamesField.getText());
            student.setNRC(nrcChoiceBox.getValue());
            student.setCreditAdvance(creditAdvanceField.getText());
        }
    }

    public void handleAssignFinalGrade() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RecordFinalGrade.fxml"));
            Parent root = loader.load();

            GUI_RecordFinalGradeController controller = loader.getController();
            controller.setStudent(student);

            Stage stage = new Stage();
            stage.setTitle("Asignar Calificación Final");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (controller.isGradeSaved()) {
                statusLabel.setText("Calificación final asignada correctamente.");
                statusLabel.setTextFill(Color.GREEN);
            } else {
                statusLabel.setText("No se asignó la calificación final.");
                statusLabel.setTextFill(Color.BLUE);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al abrir la ventana de calificación.");
            statusLabel.setTextFill(Color.RED);
        }
    }
}