package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import logic.DTO.StudentDTO;
import logic.DTO.StudentProjectDTO;
import logic.DTO.ProjectDTO;
import logic.DTO.LinkedOrganizationDTO;
import logic.DAO.StudentProjectDAO;
import logic.DAO.ProjectDAO;
import logic.DAO.LinkedOrganizationDAO;

public class GUI_DetailsStudentController {

    @FXML
    private Label labelTuition;
    @FXML
    private Label labelNames;
    @FXML
    private Label labelSurnames;
    @FXML
    private Label labelEmail;
    @FXML
    private Label labelNRC;

    // Campos para el proyecto
    @FXML
    private Label labelProjectName;
    @FXML
    private Label labelProjectDescription;
    @FXML
    private Label labelProjectOrganization;

    public void setStudent(StudentDTO student) {
        if (student != null) {
            labelTuition.setText(student.getTuition());
            labelNames.setText(student.getNames());
            labelSurnames.setText(student.getSurnames());
            labelEmail.setText(student.getEmail());
            labelNRC.setText(student.getNRC());

            // Mostrar proyecto asignado
            showAssignedProject(student);
        }
    }

    private void showAssignedProject(StudentDTO student) {
        try {
            StudentProjectDAO studentProjectDAO = new StudentProjectDAO();
            StudentProjectDTO studentProject = studentProjectDAO.searchStudentProjectByIdTuiton(student.getTuition());
            if (studentProject != null && studentProject.getIdProject() != null && !studentProject.getIdProject().isEmpty()) {
                ProjectDAO projectDAO = new ProjectDAO();
                ProjectDTO project = projectDAO.searchProjectById(studentProject.getIdProject());
                if (project != null) {
                    labelProjectName.setText(project.getName());
                    labelProjectDescription.setText(project.getDescription());

                    LinkedOrganizationDAO orgDAO = new LinkedOrganizationDAO();
                    LinkedOrganizationDTO org = orgDAO.searchLinkedOrganizationById(String.valueOf(project.getIdOrganization()));
                    labelProjectOrganization.setText(org != null ? org.getName() : "N/A");
                } else {
                    labelProjectName.setText("No asignado");
                    labelProjectDescription.setText("-");
                    labelProjectOrganization.setText("-");
                }
            } else {
                labelProjectName.setText("No asignado");
                labelProjectDescription.setText("-");
                labelProjectOrganization.setText("-");
            }
        } catch (Exception e) {
            labelProjectName.setText("Error");
            labelProjectDescription.setText("Error");
            labelProjectOrganization.setText("Error");
        }
    }
}