package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import logic.DAO.*;
import logic.DTO.DepartmentDTO;
import logic.DTO.LinkedOrganizationDTO;
import logic.DTO.ProjectDTO;
import logic.DTO.RepresentativeDTO;
import logic.DTO.StudentDTO;
import logic.DTO.StudentProjectDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    @FXML
    private Button registerSelfAssessmentButton;

    @FXML
    private Button openRegisterReportButton;

    private StudentDTO student;

    private static final Logger logger = LogManager.getLogger(GUI_AssignedProjectController.class);

    public void setStudent(StudentDTO student) {
        this.student = student;
        try {
            StudentProjectDTO studentProject = getStudentProject(student.getTuition());
            if (isStudentProjectNA(studentProject)) {
                showNoProjectAssigned();
                return;
            }

            ProjectDTO project = getProject(studentProject.getIdProject());
            if (isProjectNA(project)) {
                showProjectNotFound();
                return;
            }

            resetLabelsStyle();
            fillProjectLabels(project);
            fillOrganizationAndRepresentativeLabels(project);

            checkIfSelfAssessmentExists(student.getTuition(), Integer.parseInt(studentProject.getIdProject()));

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

    private void checkIfSelfAssessmentExists(String matricula, int idProyecto) {
        try {
            SelfAssessmentDAO dao = new SelfAssessmentDAO();
            boolean exists = dao.existsSelfAssessment(matricula, idProyecto);
            registerSelfAssessmentButton.setDisable(exists);
        } catch (Exception e) {
            registerSelfAssessmentButton.setDisable(false);
        }
    }

    private StudentProjectDTO getStudentProject(String tuiton) throws Exception {
        StudentProjectDAO studentProjectDAO = new StudentProjectDAO();
        for (StudentProjectDTO sp : studentProjectDAO.getAllStudentProjects()) {
            if (sp.getTuition().equals(tuiton)) {
                return sp;
            }
        }
        return new StudentProjectDTO("N/A", "N/A");
    }

    private boolean isStudentProjectNA(StudentProjectDTO sp) {
        return sp == null || "N/A".equals(sp.getIdProject()) || "N/A".equals(sp.getTuition());
    }

    private ProjectDTO getProject(String idProject) throws Exception {
        ProjectDAO projectDAO = new ProjectDAO();
        ProjectDTO project = projectDAO.searchProjectById(idProject);
        if (project == null) {
            return new ProjectDTO("-1", "N/A", "N/A", null, null, "N/A", 0, 0);
        }
        return project;
    }

    private boolean isProjectNA(ProjectDTO project) {
        return project == null || "-1".equals(project.getIdProject());
    }

    private void fillProjectLabels(ProjectDTO project) {
        nameLabel.setText(project.getName());
        descriptionLabel.setText(project.getDescription());
        approximateDateLabel.setText(project.getApproximateDate() != null ? project.getApproximateDate().toString() : "N/A");
        startDateLabel.setText(project.getStartDate() != null ? project.getStartDate().toString() : "N/A");
        userLabel.setText(project.getIdUser());
    }

    private void fillOrganizationAndRepresentativeLabels(ProjectDTO project) {
        try {
            LinkedOrganizationDAO orgDAO = new LinkedOrganizationDAO();
            LinkedOrganizationDTO org = orgDAO.searchLinkedOrganizationById(String.valueOf(project.getIdOrganization()));
            if (isOrganizationNA(org)) {
                organizationLabel.setText("N/A");
            } else {
                organizationLabel.setText(org.getName());
            }

            RepresentativeDAO repDAO = new RepresentativeDAO();
            RepresentativeDTO rep = getRepresentativeByDepartment(repDAO, project.getIdDepartment());
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
        return org == null || "N/A".equals(org.getIdOrganization());
    }

    private RepresentativeDTO getRepresentativeByDepartment(RepresentativeDAO repDAO, int idDepartment) throws Exception {
        for (RepresentativeDTO r : repDAO.getAllRepresentatives()) {
            if (r.getIdDepartment() != null && !r.getIdDepartment().isEmpty()) {
                try {
                    if (Integer.parseInt(r.getIdDepartment()) == idDepartment) {
                        return r;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return new RepresentativeDTO("N/A", "N/A", "N/A", "N/A", "N/A", "N/A");
    }

    private boolean isRepresentativeNA(RepresentativeDTO rep) {
        return rep == null || "N/A".equals(rep.getIdRepresentative());
    }

    private void showNoProjectAssigned() {
        nameLabel.setText("¡No tienes proyecto asignado!");
        nameLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 22px; -fx-font-weight: bold;");
        setOtherLabels("-", "-fx-text-fill: #B0B0B0;");
        checkPresentationGradeButton.setDisable(true);
        registerSelfAssessmentButton.setDisable(true);
        openRegisterReportButton.setDisable(true);
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
            StudentProjectDTO studentProject = getStudentProject(student.getTuition());
            ProjectDTO project = getProject(studentProject.getIdProject());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_RegisterSelfAssessment.fxml"));
            Parent root = loader.load();

            GUI_RegisterSelfAssessmentController controller = loader.getController();
            controller.setStudentAndProject(student, project);

            Stage stage = new Stage();
            stage.setTitle("Registrar Autoevaluación");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (NullPointerException e) {
            logger.error("NullPointerException al abrir la ventana de calificación de presentación: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de registrar autoevaluacion: {}", e.getMessage(), e);
        }
    }

    private String[] getProfessorAndPeriod(String nrc) {
        try {
            logic.DAO.GroupDAO groupDAO = new logic.DAO.GroupDAO();
            logic.DTO.GroupDTO group = groupDAO.searchGroupById(nrc);
            if (group != null) {
                return new String[]{group.getIdUser(), group.getIdPeriod()};
            }
        } catch (Exception e) {
            logger.warn("No se pudo obtener el periodo o profesor: {}", e.getMessage());
        }
        return new String[]{"N/A", "N/A"};
    }

    private String getOrganizationName(int idOrganization) {
        try {
            logic.DAO.LinkedOrganizationDAO orgDAO = new logic.DAO.LinkedOrganizationDAO();
            logic.DTO.LinkedOrganizationDTO org = orgDAO.searchLinkedOrganizationById(String.valueOf(idOrganization));
            return org != null ? org.getName() : "N/A";
        } catch (Exception e) {
            logger.warn("No se pudo obtener la organización: {}", e.getMessage());
            return "N/A";
        }
    }

    private String getProfessorNameById(String idUser) {
        try {
            logic.DAO.UserDAO userDAO = new logic.DAO.UserDAO();
            logic.DTO.UserDTO user = userDAO.searchUserById(idUser);
            if (user != null) {
                return user.getNames() + " " + user.getSurnames();
            }
        } catch (Exception e) {
            logger.warn("No se pudo obtener el nombre del profesor: {}", e.getMessage());
        }
        return "N/A";
    }

    private void showRegisterReportWindow(ProjectDTO project, String professorId, String nrc, String period, String studentName, String organization) {
        try {
            String professorName = getProfessorNameById(professorId);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_RegisterReport.fxml"));
            Parent root = loader.load();
            GUI_RegisterReportController controller = loader.getController();
            controller.setReportContext(
                    professorName,
                    nrc, period, studentName, organization,
                    project.getName(), project.getIdProject(), student.getTuition()
            );
            Stage stage = new Stage();
            stage.setTitle("Registrar Informe");
            stage.setScene(new Scene(root));
            stage.show();
            controller.setStudent(this.student);
        } catch (NullPointerException e) {
            logger.error("NullPointerException al abrir la ventana de registrar informe: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de registrar informe: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleOpenRegisterReport() {
        try {
            StudentProjectDTO studentProject = getStudentProject(student.getTuition());
            ProjectDTO project = getProject(studentProject.getIdProject());
            String nrc = student.getNRC();
            String[] profAndPeriod = getProfessorAndPeriod(nrc);
            String organization = getOrganizationName(project.getIdOrganization());
            String studentName = student.getNames() + " " + student.getSurnames();
            showRegisterReportWindow(project, profAndPeriod[0], nrc, profAndPeriod[1], studentName, organization);
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de informe: {}", e.getMessage(), e);
        }
    }
}