package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.DAO.*;
import logic.DTO.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import logic.exceptions.ProjectNotFound;

public class GUI_AssignedProjectController {

    @FXML
    private Label statusLabel;

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

        } catch (IllegalStateException e) {
            logger.error("IllegalStateException al buscar el proyecto asignado: {}", e.getMessage(), e);
            statusLabel.setText("Error de estado al buscar el proyecto asignado.");
            statusLabel.setTextFill(Color.RED);
            showErrorLoadingProject();
        }
        catch (Exception e) {
            logger.error("Error inesperado al buscar el proyecto asignado: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al buscar el proyecto asignado.");
            statusLabel.setTextFill(Color.RED);
            showErrorLoadingProject();
        }
    }

    private void checkIfSelfAssessmentExists(String matricula, int idProyecto) {
        try {
            SelfAssessmentDAO dao = new SelfAssessmentDAO();
            boolean exists = dao.existsSelfAssessment(matricula, idProyecto);
            registerSelfAssessmentButton.setDisable(exists);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                showAlert("Error de conexión con la base de datos.");
                registerSelfAssessmentButton.setDisable(true);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                logger.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                showAlert("Conexión interrumpida con la base de datos.");
                registerSelfAssessmentButton.setDisable(true);
            } else if (sqlState != null && sqlState.equals("42000")) {
                logger.error("Base de datos desconocida: {}", e.getMessage(), e);
                showAlert("Base de datos desconocida.");
                registerSelfAssessmentButton.setDisable(true);
            } else if (sqlState != null && sqlState.equals("28000")) {
                logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                showAlert("Acceso denegado a la base de datos.");
                registerSelfAssessmentButton.setDisable(true);
            } else {
                logger.error("Error al verificar autoevaluación: {}", e.getMessage(), e);
                showAlert("Error al verificar autoevaluación.");
                registerSelfAssessmentButton.setDisable(true);
            }
        }
        catch (Exception e) {
            logger.error("Error inesperado al verificar autoevaluación: {}", e.getMessage(), e);
            showAlert("Error inesperado al verificar autoevaluación.");
            registerSelfAssessmentButton.setDisable(true);
        }
    }

    private StudentProjectDTO getStudentProject(String tuition) {
        try {
            StudentProjectDAO studentProjectDAO = new StudentProjectDAO();
            for (StudentProjectDTO studentProject : studentProjectDAO.getAllStudentProjects()) {
                if (studentProject.getTuition().equals(tuition)) {
                    return studentProject;
                }
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                showAlert("Error de conexión con la base de datos.");
            } else if (sqlState != null && sqlState.equals("08S01")) {
                logger.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                showAlert("Conexión interrumpida con la base de datos.");
            } else if (sqlState != null && sqlState.equals("42000")) {
                logger.error("Base de datos desconocida: {}", e.getMessage(), e);
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                showAlert("Base de datos desconocida.");
            } else if (sqlState != null && sqlState.equals("28000")) {
                logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                showAlert("Acceso denegado a la base de datos.");
            } else {
                logger.error("Error al obtener el proyecto del estudiante de base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Error al obtener el proyecto del estudiante de base de datos.");
                statusLabel.setTextFill(Color.RED);
                showAlert("Error al obtener el proyecto del estudiante de base de datos.");
            }
        } catch (Exception e) {
            logger.error("Error inesperado al obtener el proyecto del estudiante: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al obtener el proyecto del estudiante.");
            statusLabel.setTextFill(Color.RED);
            showAlert("Error inesperado al obtener el proyecto del estudiante.");
        }
        return new StudentProjectDTO("N/A", "N/A");
    }

    private boolean isStudentProjectNA(StudentProjectDTO sp) {
        return sp == null || "N/A".equals(sp.getIdProject()) || "N/A".equals(sp.getTuition());
    }

    private ProjectDTO getProject(String idProject) {
        try {
            ProjectDAO projectDAO = new ProjectDAO();
            ProjectDTO project = projectDAO.searchProjectById(idProject);
            if (project == null) {
                return new ProjectDTO("-1", "N/A", "N/A", null, null, "N/A", 0, 0);
            }
            return project;
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                showAlert("Error de conexión con la base de datos.");
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return new ProjectDTO("-1", "N/A", "N/A", null, null, "N/A", 0, 0);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                logger.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                showAlert("Conexión interrumpida con la base de datos.");
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return new ProjectDTO("-1", "N/A", "N/A", null, null, "N/A", 0, 0);
            } else if (sqlState != null && sqlState.equals("42000")) {
                logger.error("Base de datos desconocida: {}", e.getMessage(), e);
                showAlert("Base de datos desconocida.");
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                return new ProjectDTO("-1", "N/A", "N/A", null, null, "N/A", 0, 0);
            } else if (sqlState != null && sqlState.equals("28000")) {
                logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                showAlert("Acceso denegado a la base de datos.");
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return new ProjectDTO("-1", "N/A", "N/A", null, null, "N/A", 0, 0);
            } else {
                logger.error("Error al obtener el proyecto de base de datos: {}", e.getMessage(), e);
                showAlert("Error al obtener el proyecto de base de datos.");
                statusLabel.setText("Error al obtener el proyecto de base de datos.");
                statusLabel.setTextFill(Color.RED);
                return new ProjectDTO("-1", "N/A", "N/A", null, null, "N/A", 0, 0);
            }
        }
        catch (Exception e) {
            logger.error("Error inesperado al obtener el proyecto: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al obtener el proyecto.");
            statusLabel.setTextFill(Color.RED);
            showAlert("Error inesperado al obtener el proyecto.");
            return new ProjectDTO("-1", "N/A", "N/A", null, null, "N/A", 0, 0);
        }
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
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                organizationLabel.setText("Error de conexión de base de datos");
                representativeLabel.setText("Error de conexión de base de datos");
                logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                organizationLabel.setText("Conexión interrumpida a la base de datos");
                representativeLabel.setText("Conexión interrumpida a la base de datos");
                logger.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                organizationLabel.setText("Base de datos desconocida");
                representativeLabel.setText("Base de datos desconocida");
                logger.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                organizationLabel.setText("Acceso denegado a la base de datos");
                representativeLabel.setText("Acceso denegado a la base de datos");
                logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                organizationLabel.setText("Error de base de datos al obtener organización");
                representativeLabel.setText("Error de base de datos al obtener representante");
                logger.error("Error de base de datos al obtener organización o representante: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            organizationLabel.setText("Error inesperado al obtener organización");
            representativeLabel.setText("Error inesperado al obtener representante");
            logger.error("Error inesperado al obtener organización o representante: {}", e.getMessage(), e);
        }
    }

    private boolean isOrganizationNA(LinkedOrganizationDTO org) {
        return org == null || "N/A".equals(org.getIdOrganization());
    }

    private RepresentativeDTO getRepresentativeByDepartment(RepresentativeDAO repDAO, int idDepartment) {
        try {
            for (RepresentativeDTO representativeDTO : repDAO.getAllRepresentatives()) {
                if (representativeDTO.getIdDepartment() != null && !representativeDTO.getIdDepartment().isEmpty()) {
                    try {
                        if (Integer.parseInt(representativeDTO.getIdDepartment()) == idDepartment) {
                            return representativeDTO;
                        }
                    } catch (NumberFormatException ignored) {
                        logger.warn("Error al convertir el ID del departamento a entero: {}", representativeDTO.getIdDepartment());
                        showAlert("Error al convertir el ID del departamento a entero.");
                    }
                }
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                logger.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
            } else if (sqlState != null && sqlState.equals("42000")) {
                logger.error("Base de datos desconocida: {}", e.getMessage(), e);
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
            } else if (sqlState != null && sqlState.equals("28000")) {
                logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
            } else {
                logger.error("Error de base de datos al obtener representante por departamento: {}", e.getMessage(), e);
                statusLabel.setText("Error de base de datos al obtener representante por departamento.");
                statusLabel.setTextFill(Color.RED);
            }
        } catch (Exception e) {
            logger.error("Error inesperado al obtener representante por departamento: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al obtener representante por departamento.");
            statusLabel.setTextFill(Color.RED);
        }
        return new RepresentativeDTO("N/A", "N/A", "N/A", "N/A", "N/A", "N/A", 0);
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
        checkPresentationGradeButton.setDisable(true);
        registerSelfAssessmentButton.setDisable(true);
        openRegisterReportButton.setDisable(true);
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
        } catch (IOException e) {
            logger.error("IOException al abrir la ventana de calificación de presentación: {}", e.getMessage(), e);
            showAlert("Error al abrir el fxml de la ventana de calificación de presentación.");
        } catch (NullPointerException e) {
            logger.error("NullPointerException al abrir la ventana de calificación de presentación: {}", e.getMessage(), e);
            showAlert("Recurso nulo al abrir la ventana de calificación de presentación.");
        } catch (Exception e) {
            logger.error("Error inesperado al abrir la ventana de calificación de presentación: {}", e.getMessage(), e);
            showAlert("Error inesperado al abrir la ventana de calificación de presentación.");
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
        } catch (IOException e) {
            logger.error("IOException al abrir la ventana de registrar autoevaluación: {}", e.getMessage(), e);
            showAlert("Error al abrir el fxml de la ventana de registrar autoevaluación.");
        } catch (NullPointerException e) {
            logger.error("NullPointerException al abrir la ventana de calificación de presentación: {}", e.getMessage(), e);
            showAlert("Recurso nulo al abrir la ventana de registrar autoevaluación.");
        } catch (Exception e) {
            logger.error("Error inesperado al abrir la ventana de registrar autoevaluacion: {}", e.getMessage(), e);
            showAlert("Error inesperado al abrir la ventana de registrar autoevaluación.");
        }
    }

    private String[] getProfessorAndPeriod(String nrc) {
        try {
            GroupDAO groupDAO = new GroupDAO();
            GroupDTO group = groupDAO.searchGroupById(nrc);
            if (group != null) {
                return new String[]{group.getIdUser(), group.getIdPeriod()};
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                logger.warn("Error de conexión con la base de datos al obtener el periodo o profesor: {}", e.getMessage(), e);
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                logger.warn("Conexión interrumpida con la base de datos al obtener el periodo o profesor: {}", e.getMessage(), e);
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
            } else if (sqlState != null && sqlState.equals("42000")) {
                logger.warn("Base de datos desconocida al obtener el periodo o profesor: {}", e.getMessage(), e);
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
            } else if (sqlState != null && sqlState.equals("28000")) {
                logger.warn("Acceso denegado a la base de datos al obtener el periodo o profesor: {}", e.getMessage(), e);
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
            } else {
                logger.warn("Error de base de datos al obtener el periodo o profesor: {}", e.getMessage(), e);
                statusLabel.setText("Error de base de datos.");
                statusLabel.setTextFill(Color.RED);
            }
        } catch (NullPointerException e) {
            logger.warn("NRC nulo al obtener el periodo o profesor: {}", e.getMessage(), e);
            showAlert("Error al obtener el periodo o profesor.");
        } catch (Exception e) {
            logger.warn("Error inesperado al obtener periodo o profesor: {}", e.getMessage(), e);
            showAlert("Error inesperado al obtener el periodo o profesor.");
        }
        return new String[]{"N/A", "N/A"};
    }

    private String getOrganizationName(int idOrganization) {
        try {
            logic.DAO.LinkedOrganizationDAO orgDAO = new logic.DAO.LinkedOrganizationDAO();
            logic.DTO.LinkedOrganizationDTO org = orgDAO.searchLinkedOrganizationById(String.valueOf(idOrganization));
            return org != null ? org.getName() : "N/A";
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                logger.warn("Error de conexión con la base de datos al obtener la organización: {}", e.getMessage(), e);
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Error de conexión con la base de datos.";
            } else if (sqlState != null && sqlState.equals("08S01")) {
                logger.warn("Conexión interrumpida con la base de datos al obtener la organización: {}", e.getMessage(), e);
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Conexión interrumpida con la base de datos.";
            } else if (sqlState != null && sqlState.equals("42000")) {
                logger.warn("Base de datos desconocida al obtener la organización: {}", e.getMessage(), e);
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                return "Base de datos desconocida.";
            } else if (sqlState != null && sqlState.equals("28000")) {
                logger.warn("Acceso denegado a la base de datos al obtener la organización: {}", e.getMessage(), e);
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Acceso denegado a la base de datos.";
            } else {
                logger.warn("Error de base de datos al obtener la organización: {}", e.getMessage(), e);
                statusLabel.setText("Error de base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Error de base de datos.";
            }
        } catch (NullPointerException e) {
            logger.warn("ID de organización nulo al obtener la organización: {}", e.getMessage(), e);
            statusLabel.setText("ID de organización nulo.");
            statusLabel.setTextFill(Color.RED);
            return "ID de organización nulo.";
        } catch (Exception e) {
            logger.warn("Error inesperado al obtener la organización: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al obtener la organización.");
            statusLabel.setTextFill(Color.RED);
            return "Error inesperado al obtener la organización.";
        }
    }

    private String getProfessorNameById(String idUser) {
        try {
            logic.DAO.UserDAO userDAO = new logic.DAO.UserDAO();
            logic.DTO.UserDTO user = userDAO.searchUserById(idUser);
            if (user != null) {
                return user.getNames() + " " + user.getSurnames();
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                logger.warn("Error de conexión con la base de datos al obtener el profesor: {}", e.getMessage(), e);
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Error de conexión con la base de datos.";
            } else if (sqlState != null && sqlState.equals("08S01")) {
                logger.warn("Conexión interrumpida con la base de datos al obtener el profesor: {}", e.getMessage(), e);
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Conexión interrumpida con la base de datos.";
            } else if (sqlState != null && sqlState.equals("42000")) {
                logger.warn("Base de datos desconocida al obtener el profesor: {}", e.getMessage(), e);
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                return "Base de datos desconocida.";
            } else if (sqlState != null && sqlState.equals("28000")) {
                logger.warn("Acceso denegado a la base de datos al obtener el profesor: {}", e.getMessage(), e);
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Acceso denegado a la base de datos.";
            } else {
                logger.warn("Error de base de datos al obtener el profesor: {}", e.getMessage(), e);
                statusLabel.setText("Error de base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Error de base de datos.";
            }
        } catch (NullPointerException e) {
            logger.warn("ID de usuario nulo al obtener el profesor: {}", e.getMessage(), e);
            statusLabel.setText("ID de usuario nulo.");
            statusLabel.setTextFill(Color.RED);
            return "ID de usuario nulo.";
        } catch (Exception e) {
            logger.warn("Error inesperado al obtener el profesor: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al obtener el profesor.");
            statusLabel.setTextFill(Color.RED);
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
        } catch (IOException e) {
            logger.error("IOException al abrir la ventana de registrar informe: {}", e.getMessage(), e);
            showAlert("Error al abrir el fxml de la ventana de registrar informe.");
        } catch (NullPointerException e) {
            logger.error("NullPointerException al abrir la ventana de registrar informe: {}", e.getMessage(), e);
            showAlert("Recurso nulo al abrir la ventana de registrar informe.");
        } catch (Exception e) {
            logger.error("Error inesperado al abrir la ventana de registrar informe: {}", e.getMessage(), e);
            showAlert("Error inesperado al abrir la ventana de registrar informe.");
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
        } catch (NullPointerException e) {
            logger.error("Recurso nul al abrir la ventana de informe: {}", e.getMessage(), e);
            showAlert("Recurso nulo al abrir la ventana de informe.");
        } catch (Exception e) {
            logger.error("Error inesperado al abrir la ventana de informe: {}", e.getMessage(), e);
            showAlert("Error inesperado al abrir la ventana de informe.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }
}