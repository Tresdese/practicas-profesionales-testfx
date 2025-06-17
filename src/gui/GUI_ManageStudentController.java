package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DAO.GroupDAO;
import logic.DAO.ProjectDAO;
import logic.DAO.StudentProjectDAO;
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

    @FXML
    private TextField fieldNames, fieldSurnames, fieldCreditAdvance;

    @FXML
    private ChoiceBox<String> choiceBoxNRC;

    @FXML
    private Label statusLabel;

    private StudentDTO student;
    private ProjectDTO currentProject;
    private StudentProjectDTO studentProject;
    private StudentService studentService;

    @FXML
    private void initialize() {
        try {
            this.studentService = ServiceFactory.getStudentService();
            loadNRCs();
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
                choiceBoxNRC.getItems().add(group.getNRC());
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

        fieldNames.setText(student.getNames() != null ? student.getNames() : "");
        fieldSurnames.setText(student.getSurnames() != null ? student.getSurnames() : "");
        choiceBoxNRC.setValue(student.getNRC() != null ? student.getNRC() : "");
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
            String nrc = choiceBoxNRC.getValue();
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
                choiceBoxNRC.getValue() != null &&
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