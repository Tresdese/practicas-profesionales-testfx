package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import logic.DAO.StudentProjectDAO;
import logic.DAO.ProjectDAO;
import logic.DAO.LinkedOrganizationDAO;
import logic.DAO.RepresentativeDAO;
import logic.DTO.ProjectDTO;
import logic.DTO.StudentDTO;
import logic.DTO.StudentProjectDTO;
import logic.DTO.LinkedOrganizationDTO;
import logic.DTO.RepresentativeDTO;
import logic.services.ProjectService;
import logic.services.ServiceConfig;
import logic.utils.AssignmentData;
import logic.utils.AssignmentPDFGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.mail.MessagingException;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static logic.drive.GoogleDriveFolderCreator.createOrGetFolder;
import static logic.drive.GoogleDriveUploader.uploadFile;

public class GUI_AssignProjectController {

    private static final Logger logger = LogManager.getLogger(GUI_AssignProjectController.class);

    @FXML
    private Label label;

    @FXML
    private Label studentNameLabel;

    @FXML
    private ChoiceBox<ProjectDTO> projectChoiceBox;

    @FXML
    private Button assignProjectButton;

    @FXML
    private Label statusLabel;

    private StudentDTO student;
    private ProjectService projectService;
    private StudentProjectDAO studentProjectDAO;
    private ServiceConfig serviceConfig;

    public void initialize() {
        try {
            this.serviceConfig = new ServiceConfig();
            projectService = serviceConfig.getProjectService();
            studentProjectDAO = new StudentProjectDAO();

            projectChoiceBox.setConverter(new javafx.util.StringConverter<ProjectDTO>() {
                @Override
                public String toString(ProjectDTO project) {
                    return project != null ? project.getName() : "";
                }
                @Override
                public ProjectDTO fromString(String string) {
                    for (ProjectDTO project : projectChoiceBox.getItems()) {
                        if (project.getName().equals(string)) {
                            return project;
                        }
                    }
                    return projectChoiceBox.getItems().isEmpty() ? null : projectChoiceBox.getItems().get(0);
                }
            });

            loadProjects();
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Error de conexion interrumpida.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error de conexion interrumpida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error de base de datos al cargar proyectos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error de base de datos al cargar proyectos: {}", e.getMessage(), e);
            }
        }

        catch (Exception e) {
            statusLabel.setText("Error inesperado al inicializar el controlador.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error inesperado al inicializar el controlador: {}", e.getMessage(), e);
        }
    }

    public void setStudent(StudentDTO student) {
        this.student = student;
        if (student != null) {
            studentNameLabel.setText(student.getNames() + " " + student.getSurnames());
        }
    }

    private void loadProjects() {
        try {
            List<ProjectDTO> projects = projectService.getAllProjects();
            ObservableList<ProjectDTO> observableProjects = FXCollections.observableArrayList(projects);
            projectChoiceBox.setItems(observableProjects);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar proyectos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error al cargar proyectos: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar proyectos.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error inesperado al cargar proyectos: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleAssignProject() {
        if (student == null) {
            statusLabel.setText("No se ha seleccionado un estudiante");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        ProjectDTO selectedProject = projectChoiceBox.getValue();
        if (selectedProject == null) {
            statusLabel.setText("Debe seleccionar un proyecto");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            StudentProjectDTO studentProject = new StudentProjectDTO(
                    selectedProject.getIdProject(),
                    student.getTuition()
            );
            boolean assigned = studentProjectDAO.insertStudentProject(studentProject);

            if (!assigned) {
                statusLabel.setText("No se pudo asignar el proyecto.");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            ProjectDAO projectDAO = new ProjectDAO();
            ProjectDTO project = projectDAO.searchProjectById(selectedProject.getIdProject());
            LinkedOrganizationDAO orgDAO = new LinkedOrganizationDAO();
            LinkedOrganizationDTO org = orgDAO.searchLinkedOrganizationById(String.valueOf(project.getIdOrganization()));
            RepresentativeDAO representativeDAO = new RepresentativeDAO();
            List<RepresentativeDTO> representatives = representativeDAO
                    .getRepresentativesByOrganization(String.valueOf(project.getIdOrganization()))
                    .stream()
                    .filter(representative -> {
                        if (representative.getIdDepartment() == null || representative.getIdDepartment().isEmpty()) return false;
                        try {
                            return Integer.parseInt(representative.getIdDepartment()) == project.getIdDepartment();
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    })
                    .toList();
            RepresentativeDTO rep = representatives.isEmpty()
                    ? new RepresentativeDTO("N/A", "N/A", "N/A", "N/A", "N/A", "N/A")
                    : representatives.get(0);

            AssignmentData data = new AssignmentData();
            data.setRepresentativeFirstName(rep.getNames());
            data.setRepresentativeLastName(rep.getSurnames());
            data.setOrganizationName(org.getName());
            data.setOrganizationAddress(org.getAddress());
            data.setStudentFirstName(student.getNames());
            data.setStudentLastName(student.getSurnames());
            data.setStudentTuition(student.getTuition());
            data.setProjectName(project.getName());

            String fileName = "Asignacion_" + student.getTuition() + ".pdf";
            String tempPath = System.getProperty("java.io.tmpdir") + File.separator + fileName;
            AssignmentPDFGenerator.generatePDF(tempPath, data);

            String idPeriod = getIdPeriod();
            String parentId = createDriveFolders(idPeriod);
            String driveUrl = uploadFile(tempPath, parentId);

            try {
                String subject = "Asignación de Prácticas Profesionales";
                String body = "Estimado/a " + student.getNames() + ",\n\n" +
                        "Se le informa que ha sido asignado al proyecto \"" + project.getName() + "\".\n" +
                        "Adjunto encontrará el documento oficial de asignación.\n\n" +
                        "Saludos,\nEquipo de Prácticas";
                File pdfAttachment = new File(tempPath);

                logic.gmail.GmailService.sendEmailWithAttachment(student.getEmail(), subject, body, pdfAttachment);
                logger.info("Correo enviado a " + student.getEmail());
            } catch (MessagingException ex) {
                logger.error("Error al mandar el correo (problema de mensajería): {}", ex.getMessage(), ex);
                statusLabel.setText("Error de mensajería al enviar el correo al estudiante.");
                statusLabel.setTextFill(Color.RED);
                return;
            } catch (IOException ex) {
                logger.error("Error al mandar el correo (problema de archivo o red): {}", ex.getMessage(), ex);
                statusLabel.setText("Error de archivo o red al enviar el correo al estudiante.");
                statusLabel.setTextFill(Color.RED);
                return;
            }

            statusLabel.setText("Proyecto asignado, PDF subido a Drive y correo enviado correctamente.");
            statusLabel.setTextFill(Color.GREENYELLOW);

            showSuccessAlert();
            closeWindow();
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("23000")) {
                statusLabel.setText("El proyecto ya está asignado a este estudiante.");
                statusLabel.setTextFill(Color.RED);
                logger.error("El proyecto ya está asignado a este estudiante: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al asignar el proyecto.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error al asignar el proyecto: {}", e.getMessage(), e);
            }
        } catch (IOException | GeneralSecurityException e) {
            logger.error("Error al subir PDF a Drive: {}", e.getMessage(), e);
            statusLabel.setText("Error al subir PDF a Drive.");
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al asignar el proyecto.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    private String createDriveFolders(String idPeriod) {
        try {
            String parentId = null;
            parentId = createOrGetFolder(idPeriod, parentId);
            parentId = createOrGetFolder(student.getNRC(), parentId);
            parentId = createOrGetFolder(student.getTuition(), parentId);
            parentId = createOrGetFolder("Asignacion", parentId);
            return parentId;
        } catch (IOException | GeneralSecurityException e) {
            logger.error("Error al crear carpetas en Drive: {}", e.getMessage(), e);
            showAlert("Error al crear carpetas en Drive: ");
            return null;
        } catch (Exception e) {
            logger.error("Error inesperado al crear carpetas en Drive: {}", e.getMessage(), e);
            showAlert("Error inesperado al crear carpetas en Drive: ");
            return null;
        }
    }

    private String getIdPeriod() {
        try {
            logic.DAO.GroupDAO groupDAO = new logic.DAO.GroupDAO();
            logic.DTO.GroupDTO group = groupDAO.searchGroupById(student.getNRC());
            return (group != null && group.getIdPeriod() != null) ? group.getIdPeriod() : "PeriodoDesconocido";
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                logger.warn("Error de conexión con la base de datos: {}", e.getMessage(), e);
                showAlert("Error de conexión con la base de datos: ");
                return "PeriodoDesconocido";
            } else if (sqlState != null && sqlState.equals("08S01")) {
                logger.warn("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                showAlert("Conexión interrumpida con la base de datos: ");
                return "PeriodoDesconocido";
            } else if (sqlState != null && sqlState.equals("42000")) {
                logger.warn("Base de datos desconocida: {}", e.getMessage(), e);
                showAlert("Base de datos desconocida: ");
                return "PeriodoDesconocido";
            } else if (sqlState != null && sqlState.equals("28000")) {
                logger.warn("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                showAlert("Acceso denegado a la base de datos: ");
                return "PeriodoDesconocido";
            } else {
                logger.warn("Error al obtener el periodo del grupo de la base de datos: {}", e.getMessage(), e);
                showAlert("Error al obtener el periodo del grupo de la base de datos: ");
                return "PeriodoDesconocido";
            }
        } catch (Exception e) {
            logger.warn("Error inesperado. No se pudo obtener el periodo del grupo {}", e.getMessage(), e);
            showAlert("Error inesperado al obtener el periodo del grupo: ");
            return "PeriodoDesconocido";
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) assignProjectButton.getScene().getWindow();
        stage.close();
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText("¡La asignación se realizó exitosamente!");
        alert.showAndWait();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }
}