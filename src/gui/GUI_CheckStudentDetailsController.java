package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import logic.DAO.ProjectDAO;
import logic.DTO.ProjectDTO;
import logic.DTO.StudentDTO;

public class GUI_CheckStudentDetailsController {

    @FXML
    private Label labelTuition, labelNames, labelSurnames, labelNRC, labelCreditAdvance, labelFinalGrade, labelProjectId, labelProjectName, statusLabel;

    private StudentDTO student;

    public void setStudentData(StudentDTO student) {
        if (student == null) {
            statusLabel.setText("No se encontró información del estudiante.");
            return;
        }
        this.student = student;
        labelTuition.setText(student.getTuition() != null ? student.getTuition() : "");
        labelNames.setText(student.getNames() != null ? student.getNames() : "");
        labelSurnames.setText(student.getSurnames() != null ? student.getSurnames() : "");
        labelNRC.setText(student.getNRC() != null ? student.getNRC() : "");
        labelCreditAdvance.setText(student.getCreditAdvance() != null ? student.getCreditAdvance() : "");
        labelFinalGrade.setText(String.valueOf(student.getFinalGrade()));

        String nrc = student.getNRC();
        if (nrc != null && !nrc.isEmpty()) {
            try {
                ProjectDAO projectDAO = new ProjectDAO();
                ProjectDTO project = projectDAO.searchProjectById(nrc);
                labelProjectId.setText(project.getIdProject());
                labelProjectName.setText(project.getName());
            } catch (Exception e) {
                labelProjectId.setText("N/A");
                labelProjectName.setText("No encontrado");
                statusLabel.setText("Error al obtener el proyecto.");
            }
        } else {
            labelProjectId.setText("N/A");
            labelProjectName.setText("N/A");
        }

        statusLabel.setText("");
    }
}