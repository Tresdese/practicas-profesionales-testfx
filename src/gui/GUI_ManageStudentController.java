package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DAO.ProjectDAO;
import logic.DAO.StudentProjectDAO;
import logic.DTO.ProjectDTO;
import logic.DTO.StudentDTO;
import logic.DTO.StudentProjectDTO;
import logic.services.ServiceFactory;
import logic.services.StudentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class GUI_ManageStudentController {

    private static final Logger logger = LogManager.getLogger(GUI_ManageStudentController.class);

    @FXML
    private TextField fieldNames, fieldSurnames, fieldNRC, fieldCreditAdvance;

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
        } catch (Exception e) {
            logger.error("Error al obtener StudentService desde ServiceFactory: {}", e.getMessage(), e);
            statusLabel.setText("Error al conectar con la base de datos.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
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

    private ProjectDTO getCurrentProjectForStudent(StudentDTO student) {
        try {
            StudentProjectDAO studentProjectDAO = new StudentProjectDAO();
            StudentProjectDTO studentProject = studentProjectDAO.searchStudentProjectByIdTuiton(student.getTuiton());
            if (studentProject != null && studentProject.getIdProject() != null && !studentProject.getIdProject().isEmpty()) {
                ProjectDAO projectDAO = new ProjectDAO();
                return projectDAO.searchProjectById(studentProject.getIdProject());
            }
        } catch (Exception e) {
            logger.error("Error al obtener el proyecto actual del estudiante: {}", e.getMessage(), e);
        }
        return null;
    }

    @FXML
    private void handleReassignProject(ActionEvent event) {
        if (student == null) {
            statusLabel.setText("No hay un estudiante seleccionado.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }
        try {
            ProjectDTO currentProject = getCurrentProjectForStudent(student);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_ReassignProject.fxml"));
            Parent root = loader.load();

            GUI_ReassignProjectController controller = loader.getController();
            controller.setProjectStudent(student, currentProject);

            Stage stage = new Stage();
            stage.setTitle("Reasignar Proyecto");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.error("Error al cargar la interfaz GUI_ReassignProject.fxml: {}", e.getMessage(), e);
            statusLabel.setText("Error al abrir la ventana de reasignación.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    private boolean areFieldsFilled() {
        return !fieldNames.getText().isEmpty() &&
                !fieldSurnames.getText().isEmpty() &&
                !fieldNRC.getText().isEmpty() &&
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