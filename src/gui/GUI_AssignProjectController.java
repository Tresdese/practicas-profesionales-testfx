package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
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
    private Button buttonAssignProject;

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
        } catch (Exception e) {
            logger.error("Error al inicializar el controlador: {}", e.getMessage(), e);
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
            logger.error("Error al cargar proyectos: {}", e.getMessage(), e);
            statusLabel.setText("Error al cargar proyectos.");
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
                logger.info("Email sent to " + student.getEmail());
            } catch (jakarta.mail.MessagingException | IOException ex) {
                logger.error("Error sending email: {}", ex.getMessage(), ex);
                statusLabel.setText("Error sending email to the student.");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            statusLabel.setText("Proyecto asignado, PDF subido a Drive y correo enviado correctamente.");
            statusLabel.setStyle("-fx-text-fill: green;");

            closeWindow();
        } catch (SQLException e) {
            logger.error("Error de SQL al asignar proyecto: {}", e.getMessage(), e);
            statusLabel.setText("Error de base de datos.");
            statusLabel.setStyle("-fx-text-fill: red;");
        } catch (IOException | GeneralSecurityException e) {
            logger.error("Error al subir PDF a Drive: {}", e.getMessage(), e);
            statusLabel.setText("Error al subir PDF a Drive.");
            statusLabel.setStyle("-fx-text-fill: red;");
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado.");
            statusLabel.setStyle("-fx-text-fill: red;");
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
            return null;
        }
    }

    private String getIdPeriod() {
        try {
            logic.DAO.GroupDAO groupDAO = new logic.DAO.GroupDAO();
            logic.DTO.GroupDTO group = groupDAO.searchGroupById(student.getNRC());
            return (group != null && group.getIdPeriod() != null) ? group.getIdPeriod() : "PeriodoDesconocido";
        } catch (Exception e) {
            logger.warn("No se pudo obtener el periodo del grupo", e);
            return "PeriodoDesconocido";
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) buttonAssignProject.getScene().getWindow();
        stage.close();
    }
}