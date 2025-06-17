package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DAO.GroupDAO;
import logic.DTO.GroupDTO;
import logic.DTO.ProjectDTO;
import logic.DTO.StudentDTO;
import logic.DTO.StudentProjectDTO;
import logic.services.ServiceFactory;
import logic.services.StudentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GUI_ManageStudentController {

    private static final Logger logger = LogManager.getLogger(GUI_ManageStudentController.class);

    private static final int MAX_NAMES = 50;
    private static final int MAX_SURNAMES = 50;

    @FXML
    private TextField namesField, surnamesField, fieldCreditAdvance;

    @FXML
    private ChoiceBox<String> nrcChoiceBox;

    @FXML
    private Label statusLabel, namesCharCountLabel, surnamesCharCountLabel;

    private StudentDTO student;
    private ProjectDTO currentProject;
    private StudentProjectDTO studentProject;
    private StudentService studentService;

    @FXML
    private void initialize() {
        try {
            this.studentService = ServiceFactory.getStudentService();
            loadNRCs();
            namesField.setTextFormatter(new TextFormatter<>(change ->
                    change.getControlNewText().length() <= MAX_NAMES ? change : null));
            namesCharCountLabel.setText("0/" + MAX_NAMES);
            namesField.textProperty().addListener((observable, oldText, newText) ->
                    namesCharCountLabel.setText(newText.length() + "/" + MAX_NAMES)
            );

            surnamesField.setTextFormatter(new TextFormatter<>(change ->
                    change.getControlNewText().length() <= MAX_SURNAMES ? change : null));
            surnamesCharCountLabel.setText("0/" + MAX_SURNAMES);
            surnamesField.textProperty().addListener((observable, oldText, newText) ->
                    surnamesCharCountLabel.setText(newText.length() + "/" + MAX_SURNAMES)
            );
        } catch (Exception e) {
            logger.error("Error al obtener StudentService desde ServiceFactory: {}", e.getMessage(), e);
            statusLabel.setText("Error al conectar con la base de datos.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    private void loadNRCs() {
        try {
            GroupDAO groupDAO = new GroupDAO();
            List<GroupDTO> groups = groupDAO.getAllGroups();
            for (GroupDTO group : groups) {
                nrcChoiceBox.getItems().add(group.getNRC());
            }
        } catch (SQLException e) {
            logger.error("Error al cargar los NRCs: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al cargar los NRCs: {}", e.getMessage(), e);
        }
    }

    public void setStudentData(StudentDTO student, ProjectDTO currentProject) {
        if (student == null) {
            logger.error("El objeto StudentDTO es nulo.");
            return;
        }

        this.student = student;
        this.currentProject = currentProject;

        namesField.setText(student.getNames() != null ? student.getNames() : "");
        surnamesField.setText(student.getSurnames() != null ? student.getSurnames() : "");
        nrcChoiceBox.setValue(student.getNRC() != null ? student.getNRC() : "");
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

            String names = namesField.getText();
            String surnames = surnamesField.getText();
            String nrc = nrcChoiceBox.getValue();
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
        return !namesField.getText().isEmpty() &&
                !surnamesField.getText().isEmpty() &&
                nrcChoiceBox.getValue() != null &&
                !fieldCreditAdvance.getText().isEmpty();
    }

    @FXML
    private void handleAssignFinalGrade() {
        if (student == null) {
            statusLabel.setText("No hay un estudiante seleccionado.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RecordFinalGrade.fxml"));
            Parent root = loader.load();

            GUI_RecordFinalGradeController controller = loader.getController();
            controller.setStudent(student);

            Stage stage = new Stage();
            stage.setTitle("Asignar Calificación Final");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.error("Error al cargar la interfaz GUI_RecordFinalGrade.fxml: {}", e.getMessage(), e);
            statusLabel.setText("Error al abrir la ventana de calificación.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }
}