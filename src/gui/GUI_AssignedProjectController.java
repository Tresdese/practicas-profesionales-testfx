package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import logic.DAO.ProjectDAO;
import logic.DAO.StudentProjectDAO;
import logic.DTO.ProjectDTO;
import logic.DTO.StudentDTO;
import logic.DTO.StudentProjectDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GUI_AssignedProjectController {

    @FXML
    private Label nameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label approximateDateLabel;
    @FXML
    private Label startDateLabel;
    @FXML
    private Label userLabel;

    private static final Logger logger = LogManager.getLogger(GUI_AssignedProjectController.class);

    public void setStudent(StudentDTO student) {
        try {
            StudentProjectDAO studentProjectDAO = new StudentProjectDAO();
            StudentProjectDTO studentProject = null;
            // Buscar el proyecto asignado por matrícula
            for (StudentProjectDTO sp : studentProjectDAO.getAllStudentProjects()) {
                if (sp.getTuiton().equals(student.getTuiton())) {
                    studentProject = sp;
                    break;
                }
            }
            if (studentProject != null) {
                ProjectDAO projectDAO = new ProjectDAO();
                ProjectDTO project = projectDAO.searchProjectById(studentProject.getIdProject());
                if (project != null) {
                    nameLabel.setText(project.getName());
                    descriptionLabel.setText(project.getDescription());
                    approximateDateLabel.setText((project.getApproximateDate() != null ? project.getApproximateDate().toString() : "N/A"));
                    startDateLabel.setText((project.getStartDate() != null ? project.getStartDate().toString() : "N/A"));
                    userLabel.setText(project.getIdUser());
                } else {
                    nameLabel.setText("No se encontró el proyecto.");
                }
            } else {
                nameLabel.setText("No tienes proyecto asignado.");
            }
        } catch (Exception e) {
            logger.error("Error al buscar el proyecto asignado: {}", e.getMessage(), e);
            nameLabel.setText("Error al cargar el proyecto.");
        }
    }
}