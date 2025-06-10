package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import logic.DAO.ProjectDAO;
import logic.DAO.StudentProjectDAO;
import logic.DTO.StudentProjectDTO;
import logic.DTO.ProjectDTO;
import logic.DTO.StudentDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class GUI_ReassignProjectController {
    private static final Logger logger = LogManager.getLogger(GUI_ReassignProjectController.class);

    @FXML
    private Label studentNameLabel;
    @FXML
    private ChoiceBox<ProjectDTO> projectChoiceBox;
    @FXML
    private Button buttonAssignProject;
    @FXML
    private Label statusLabel;

    private StudentDTO student;
    private ProjectDTO currentProject;

    @FXML
    private void initialize() {
        buttonAssignProject.setOnAction(event -> handleReassignProject());
    }

    @FXML
    public void handleReassignProject() {
        ProjectDTO selectedProject = projectChoiceBox.getValue();
        if (selectedProject == null) {
            statusLabel.setText("Seleccione un proyecto.");
            return;
        }
        try {
            StudentProjectDAO studentProjectDAO = new StudentProjectDAO();
            StudentProjectDTO studentProjectDTO = new StudentProjectDTO(selectedProject.getIdProject(), student.getTuition());
            studentProjectDAO.updateStudentProject(studentProjectDTO);
            statusLabel.setText("Proyecto reasignado correctamente.");
            closeWindow();
        } catch (SQLException e) {
            logger.error("Error al reasignar proyecto: {}", e.getMessage(), e);
            statusLabel.setText("Error al reasignar proyecto.");
        }
    }

    public void setProjectStudent(StudentDTO student, ProjectDTO currentProject) {
        this.student = student;
        this.currentProject = currentProject;
        studentNameLabel.setText(student.getNames() + " " + student.getSurnames());
        loadAvailableProjects();
        if (currentProject != null) {
            projectChoiceBox.setValue(currentProject);
        }
    }

    private void loadAvailableProjects() {
        try {
            ProjectDAO projectDAO = new ProjectDAO();
            List<ProjectDTO> allProjects = projectDAO.getAllProjects();
            ObservableList<ProjectDTO> projectList = FXCollections.observableArrayList(allProjects);
            projectChoiceBox.setItems(projectList);
        } catch (SQLException e) {
            logger.error("Error al cargar proyectos disponibles: {}", e.getMessage(), e);
            statusLabel.setText("Error al cargar proyectos.");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) buttonAssignProject.getScene().getWindow();
        stage.close();
    }
}