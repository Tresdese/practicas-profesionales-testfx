package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import logic.DAO.LinkedOrganizationDAO;
import logic.DAO.ProjectDAO;
import logic.DAO.RepresentativeDAO;
import logic.DAO.StudentProjectDAO;
import logic.DTO.LinkedOrganizationDTO;
import logic.DTO.ProjectDTO;
import logic.DTO.RepresentativeDTO;
import logic.DTO.StudentDTO;
import logic.DTO.StudentProjectDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import data_access.ConecctionDataBase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import logic.exceptions.ProjectNotFound;

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
    @FXML
    private Label organizationLabel;
    @FXML
    private Label representativeLabel;
    @FXML
    private Button checkPresentationGradeButton;

    private StudentDTO student;
    private StudentProjectDTO studentProject;

    private static final Logger logger = LogManager.getLogger(GUI_AssignedProjectController.class);

    public void setStudent(StudentDTO student) {
        this.student = student;
        try {
            this.studentProject = getStudentProject(student.getTuiton());
            if (this.studentProject == null) {
                showNoProjectAssigned();
                return;
            }

            ProjectDTO project = getProjectFromStudentProject(this.studentProject);
            if (project == null) {
                showProjectNotFound();
                return;
            }

            resetLabelsStyle();
            fillProjectLabels(project);
            fillOrganizationAndRepresentativeLabels(project.getIdOrganization());
        } catch (ProjectNotFound e) {
            logger.warn("Proyecto no encontrado: {}", e.getMessage());
            showProjectNotFound();
        } catch (SQLException e) {
            logger.error("Error de base de datos al buscar el proyecto asignado: {}", e.getMessage(), e);
            showErrorLoadingProject();
        } catch (Exception e) {
            logger.error("Error inesperado al buscar el proyecto asignado: {}", e.getMessage(), e);
            showErrorLoadingProject();
        }
    }

    private StudentProjectDTO getStudentProject(String tuiton) throws Exception {
        StudentProjectDAO studentProjectDAO = new StudentProjectDAO();
        StudentProjectDTO sp = studentProjectDAO.searchStudentProjectByIdTuiton(tuiton);
        if (sp == null || sp.getIdProject() == null || sp.getIdProject().isEmpty() || "N/A".equals(sp.getIdProject())) {
            return null;
        }
        return sp;
    }

    private ProjectDTO getProjectFromStudentProject(StudentProjectDTO sp) throws Exception {
        if (sp == null) {
            return null;
        }
        ProjectDAO projectDAO = new ProjectDAO();
        ProjectDTO project = projectDAO.searchProjectById(sp.getIdProject());
        if (project == null || project.getIdProject() == null || project.getIdProject().isEmpty()) {
            return null;
        }
        return project;
    }

    private void fillProjectLabels(ProjectDTO project) {
        nameLabel.setText(project.getName());
        descriptionLabel.setText(project.getDescription());
        approximateDateLabel.setText(project.getApproximateDate() != null ? project.getApproximateDate().toString() : "N/A");
        startDateLabel.setText(project.getStartDate() != null ? project.getStartDate().toString() : "N/A");
        userLabel.setText(project.getIdUser());
    }

    private void fillOrganizationAndRepresentativeLabels(int idOrganization) {
        try (ConecctionDataBase db = new ConecctionDataBase();
             Connection conn = db.connectDB()) {

            LinkedOrganizationDAO orgDAO = new LinkedOrganizationDAO();
            LinkedOrganizationDTO org = orgDAO.searchLinkedOrganizationById(String.valueOf(idOrganization));
            if (isOrganizationNA(org)) {
                organizationLabel.setText("N/A");
            } else {
                organizationLabel.setText(org.getName());
            }

            RepresentativeDAO repDAO = new RepresentativeDAO();
            RepresentativeDTO rep = getRepresentativeByOrganization(repDAO, org.getIddOrganization());
            if (isRepresentativeNA(rep)) {
                representativeLabel.setText("No asignado");
            } else {
                representativeLabel.setText(rep.getNames() + " " + rep.getSurnames());
            }
        } catch (SQLException e) {
            organizationLabel.setText("Error");
            representativeLabel.setText("Error");
            logger.error("Error de base de datos al obtener organización o representante: {}", e.getMessage(), e);
        } catch (Exception e) {
            organizationLabel.setText("Error");
            representativeLabel.setText("Error");
            logger.error("Error inesperado al obtener organización o representante: {}", e.getMessage(), e);
        }
    }

    private boolean isOrganizationNA(LinkedOrganizationDTO org) {
        return org == null || "N/A".equals(org.getIddOrganization());
    }

    private RepresentativeDTO getRepresentativeByOrganization(RepresentativeDAO repDAO, String idOrganization) throws Exception {
        for (RepresentativeDTO r : repDAO.getAllRepresentatives()) {
            if (r.getIdOrganization().equals(idOrganization)) {
                return r;
            }
        }
        return new RepresentativeDTO("N/A", "N/A", "N/A", "N/A", "N/A");
    }

    private boolean isRepresentativeNA(RepresentativeDTO rep) {
        return rep == null || "N/A".equals(rep.getIdRepresentative());
    }

    private void showNoProjectAssigned() {
        nameLabel.setText("¡No tienes proyecto asignado!");
        nameLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 22px; -fx-font-weight: bold;");
        setOtherLabels("-", "-fx-text-fill: #B0B0B0;");
    }

    private void showProjectNotFound() {
        nameLabel.setText("No se encontró el proyecto.");
        nameLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 18px; -fx-font-weight: bold;");
        setOtherLabels("-", "-fx-text-fill: #B0B0B0;");
    }

    private void showErrorLoadingProject() {
        nameLabel.setText("Error al cargar el proyecto.");
        nameLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 18px; -fx-font-weight: bold;");
        setOtherLabels("-", "-fx-text-fill: #B0B0B0;");
    }

    private void setOtherLabels(String text, String style) {
        descriptionLabel.setText(text);
        approximateDateLabel.setText(text);
        startDateLabel.setText(text);
        userLabel.setText(text);
        organizationLabel.setText(text);
        representativeLabel.setText(text);

        descriptionLabel.setStyle(style);
        approximateDateLabel.setStyle(style);
        startDateLabel.setStyle(style);
        userLabel.setStyle(style);
        organizationLabel.setStyle(style);
        representativeLabel.setStyle(style);
    }

    private void resetLabelsStyle() {
        nameLabel.setStyle("-fx-text-fill: #333; -fx-font-size: 16px; -fx-font-weight: normal;");
        descriptionLabel.setStyle("-fx-text-fill: #333;");
        approximateDateLabel.setStyle("-fx-text-fill: #333;");
        startDateLabel.setStyle("-fx-text-fill: #333;");
        userLabel.setStyle("-fx-text-fill: #333;");
        organizationLabel.setStyle("-fx-text-fill: #333;");
        representativeLabel.setStyle("-fx-text-fill: #333;");
    }

    @FXML
    private void handleCheckPresentationGrade() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_CheckPresentationGrade.fxml"));
            Parent root = loader.load();

            GUI_CheckPresentationGradeController controller = loader.getController();
            controller.setStudent(this.student);

            Stage stage = new Stage();
            stage.setTitle("Calificación de Presentación");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (NullPointerException e) {
            logger.error("NullPointerException al abrir la ventana de calificación de presentación: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de calificación de presentación: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleOpenSelfAssessment() {
        try {
            ProjectDTO project = getProjectFromStudentProject(studentProject);
            if (project == null) {
                logger.error("No se pudo obtener el proyecto para la autoevaluación");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_RegisterSelfAssessment.fxml"));
            Parent root = loader.load();

            GUI_RegisterSelfAssessmentController controller = loader.getController();
            controller.setStudentAndProject(student, project);

            Stage stage = new Stage();
            stage.setTitle("Registrar Autoevaluación");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (ProjectNotFound e) {
            logger.error("Proyecto no encontrado para autoevaluación: {}", e.getMessage(), e);
        } catch (IOException e) {
            logger.error("Error al cargar el archivo FXML: {}", e.getMessage(), e);
        } catch (NullPointerException e) {
            logger.error("Recurso FXML o controlador no encontrado: {}", e.getMessage(), e);
        } catch (IllegalStateException e) {
            logger.error("Error en el estado de JavaFX: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al abrir la ventana de autoevaluación: {}", e.getMessage(), e);
        }
    }
}