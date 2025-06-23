package gui;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.itextpdf.text.DocumentException;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.SendFailedException;
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
import java.io.FileNotFoundException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
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
            } else if (sqlState != null && sqlState.equals("42S02")) {
                logger.error("Tabla de autoevaluación no encontrada: {}", e.getMessage(), e);
                showAlert("Tabla de autoevaluación no encontrada en la base de datos.");
            } else if (sqlState != null && sqlState.equals("42S22")) {
                logger.error("Columna no encontrada en la tabla de autoevaluación: {}", e.getMessage(), e);
                showAlert("Columna no encontrada en la tabla de autoevaluación.");
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar proyectos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error al cargar proyectos: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al leer el archivo de configuracion de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error al leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar proyectos.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error inesperado al cargar proyectos: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleAssignProject() {
        if (!validateInputs()) {
            return;
        }

        if (!assignProjectToStudent()) {
            return;
        }

        String[] pdfPathHolder = new String[1];
        if (!generateAssignmentPDF(pdfPathHolder)) {
            return;
        }

        String[] driveUrlHolder = new String[1];
        if (!uploadPDFToDrive(pdfPathHolder[0], driveUrlHolder)) {
            return;
        }

        if (!sendNotificationEmail(pdfPathHolder[0])) {
            return;
        }

        statusLabel.setText("Proyecto asignado, PDF subido a Drive y correo enviado correctamente.");
        statusLabel.setTextFill(Color.GREENYELLOW);
        showSuccessAlert();
        closeWindow();
    }

    private boolean validateInputs() {
        if (student == null) {
            statusLabel.setText("No se ha seleccionado un estudiante");
            statusLabel.setStyle("-fx-text-fill: red;");
            return false;
        }
        if (projectChoiceBox.getValue() == null) {
            statusLabel.setText("Debe seleccionar un proyecto");
            statusLabel.setStyle("-fx-text-fill: red;");
            return false;
        }
        return true;
    }

    private boolean assignProjectToStudent() {
        try {
            ProjectDTO selectedProject = projectChoiceBox.getValue();
            StudentProjectDTO studentProject = new StudentProjectDTO(selectedProject.getIdProject(), student.getTuition());
            boolean assigned = studentProjectDAO.insertStudentProject(studentProject);
            if (!assigned) {
                statusLabel.setText("No se pudo asignar el proyecto.");
                statusLabel.setStyle("-fx-text-fill: red;");
                return false;
            }
            return true;
        } catch (SQLException e) {
            handleSQLException(e, "asignar el proyecto");
            return false;
        } catch (IOException e) {
            statusLabel.setText("Error al leer el archivo de configuración de la base de datos.");
            statusLabel.setStyle("-fx-text-fill: red;");
            logger.error("Error al leer el archivo de configuración de la base de datos: {}", e.getMessage(), e);
            return false;
        }
        catch (Exception e) {
            statusLabel.setText("Error inesperado al asignar el proyecto.");
            statusLabel.setStyle("-fx-text-fill: red;");
            logger.error("Error inesperado al asignar el proyecto: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean generateAssignmentPDF(String[] pdfPathHolder) {
        try {
            ProjectDTO selectedProject = projectChoiceBox.getValue();
            AssignmentData data = prepareAssignmentData(selectedProject);
            String fileName = "Asignacion_" + student.getTuition() + ".pdf";
            String tempPath = System.getProperty("java.io.tmpdir") + File.separator + fileName;
            AssignmentPDFGenerator.generatePDF(tempPath, data);
            pdfPathHolder[0] = tempPath;
            return true;
        } catch (IOException e) {
            logger.error("Error de entrada/salida al generar el PDF de asignación: {}", e.getMessage(), e);
            statusLabel.setText("Error de entrada/salida al generar el PDF de asignación.");
            statusLabel.setTextFill(Color.RED);
            return false;
        } catch (DocumentException e) {
            logger.error("Error al crear el documento PDF: {}", e.getMessage(), e);
            statusLabel.setText("Error al crear el documento PDF.");
            statusLabel.setTextFill(Color.RED);
            return false;
        } catch (Exception e) {
            logger.error("Error al generar el PDF de asignación: {}", e.getMessage(), e);
            statusLabel.setText("Error al generar el PDF de asignación.");
            statusLabel.setTextFill(Color.RED);
            return false;
        }
    }

    private boolean uploadPDFToDrive(String pdfPath, String[] driveUrlHolder) {
        try {
            String idPeriod = getIdPeriod();
            String parentId = createDriveFolders(idPeriod);
            String driveUrl = uploadFile(pdfPath, parentId);
            driveUrlHolder[0] = driveUrl;
            return true;
        } catch (UnknownHostException e) {
            logger.error("Error de red al subir PDF a Drive: {}", e.getMessage(), e);
            statusLabel.setText("Error de red al subir PDF a Drive.");
            statusLabel.setTextFill(Color.RED);
            return false;
        } catch (SocketTimeoutException e) {
            logger.error("Tiempo de espera agotado al subir PDF a Drive: {}", e.getMessage(), e);
            statusLabel.setText("Tiempo de espera agotado al subir PDF a Drive.");
            statusLabel.setTextFill(Color.RED);
            return false;
        } catch (FileNotFoundException e) {
            logger.error("Archivo no encontrado al subir PDF a Drive: {}", e.getMessage(), e);
            statusLabel.setText("Archivo no encontrado al subir PDF a Drive.");
            statusLabel.setTextFill(Color.RED);
            return false;
        } catch (GoogleJsonResponseException e) {
            logger.error("Error de Google Drive al subir PDF: {}", e.getDetails().getMessage(), e);
            statusLabel.setText("Error de Google Drive al subir PDF.");
            statusLabel.setTextFill(Color.RED);
            return false;
        } catch (GeneralSecurityException e) {
            logger.error("Error de seguridad al subir PDF a Drive: {}", e.getMessage(), e);
            statusLabel.setText("Error de seguridad al subir PDF a Drive.");
            statusLabel.setTextFill(Color.RED);
            return false;
        } catch (IOException e) {
            logger.error("Error entrada/salida al subir PDF a Drive: {}", e.getMessage(), e);
            statusLabel.setText("Error entrada/salida al subir PDF a Drive.");
            statusLabel.setTextFill(Color.RED);
            return false;
        } catch (Exception e) {
            logger.error("Error al subir PDF a Drive: {}", e.getMessage(), e);
            statusLabel.setText("Error al subir PDF a Drive.");
            statusLabel.setTextFill(Color.RED);
            return false;
        }
    }

    private boolean sendNotificationEmail(String pdfPath) {
        try {
            ProjectDTO selectedProject = projectChoiceBox.getValue();
            String subject = "Asignación de Prácticas Profesionales";
            String body = "Estimado/a " + student.getNames() + ",\n\n" +
                    "Se le informa que ha sido asignado al proyecto \"" + selectedProject.getName() + "\".\n" +
                    "Adjunto encontrará el documento oficial de asignación.\n\n" +
                    "Saludos,\nEquipo de Prácticas";
            File pdfAttachment = new File(pdfPath);
            logic.gmail.GmailService.sendEmailWithAttachment(student.getEmail(), subject, body, pdfAttachment);
            logger.info("Correo enviado a " + student.getEmail());
            return true;
        } catch (AuthenticationFailedException e) {
            logger.error("Error de autenticación al enviar el correo: {}", e.getMessage(), e);
            statusLabel.setText("Error de autenticación al enviar el correo.");
            statusLabel.setTextFill(Color.RED);
            return false;
        } catch (SendFailedException e) {
            logger.error("Error al enviar el correo: {}", e.getMessage(), e);
            statusLabel.setText("Error al enviar el correo al estudiante.");
            statusLabel.setTextFill(Color.RED);
            return false;
        } catch (MessagingException e) {
            logger.error("Error de mensajería al enviar el correo: {}", e.getMessage(), e);
            statusLabel.setText("Error de mensajería al enviar el correo.");
            statusLabel.setTextFill(Color.RED);
            return false;
        } catch (IOException e) {
            logger.error("Error de entrada/salida al enviar el correo: {}", e.getMessage(), e);
            statusLabel.setText("Error de entrada/salida al enviar el correo.");
            statusLabel.setTextFill(Color.RED);
            return false;
        } catch (Exception e) {
            logger.error("Error al enviar el correo de notificación: {}", e.getMessage(), e);
            statusLabel.setText("Error al enviar el correo al estudiante.");
            statusLabel.setTextFill(Color.RED);
            return false;
        }
    }

    private AssignmentData prepareAssignmentData(ProjectDTO project) {
        try {
            LinkedOrganizationDAO orgDAO = new LinkedOrganizationDAO();
            RepresentativeDAO representativeDAO = new RepresentativeDAO();
            LinkedOrganizationDTO org = orgDAO.searchLinkedOrganizationById(String.valueOf(project.getIdOrganization()));
            RepresentativeDTO rep = representativeDAO.getRepresentativesByOrganization(String.valueOf(project.getIdOrganization()))
                    .stream()
                    .findFirst()
                    .orElse(new RepresentativeDTO("N/A", "N/A", "N/A", "N/A", "N/A", "N/A", 0));
            AssignmentData data = new AssignmentData();
            data.setRepresentativeFirstName(rep.getNames());
            data.setRepresentativeLastName(rep.getSurnames());
            data.setOrganizationName(org.getName());
            data.setOrganizationAddress(org.getAddress());
            data.setStudentFirstName(student.getNames());
            data.setStudentLastName(student.getSurnames());
            data.setStudentTuition(student.getTuition());
            data.setProjectName(project.getName());
            return data;
        } catch (SQLException e) {
            handleSQLException(e, "preparar los datos de asignación");
            return new AssignmentData();
        } catch (IOException e) {
            logger.error("Error al leer el archivo de configuración de la base de datos: {}", e.getMessage(), e);
            statusLabel.setText("Error al leer el archivo de configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            return new AssignmentData();
        } catch (Exception e) {
            logger.error("Error al preparar los datos de asignación: {}", e.getMessage(), e);
            statusLabel.setText("Error al preparar los datos de asignación.");
            statusLabel.setTextFill(Color.RED);
            return new AssignmentData();
        }
    }

    private void handleSQLException(SQLException e, String context) {
        String sqlState = e.getSQLState();
        if ("08001".equals(sqlState)) {
            statusLabel.setText("Error de conexión con la base de datos.");
        } else if ("08S01".equals(sqlState)) {
            statusLabel.setText("Conexión interrumpida con la base de datos.");
        } else if ("42000".equals(sqlState)) {
            statusLabel.setText("Base de datos desconocida.");
        } else if ("42S02".equals(sqlState)) {
            statusLabel.setText("Tabla no encontrada.");
        } else if ("42S22".equals(sqlState)) {
            statusLabel.setText("Columna no encontrada.");
        } else if ("28000".equals(sqlState)) {
            statusLabel.setText("Acceso denegado a la base de datos.");
        } else if ("23000".equals(sqlState)) {
            statusLabel.setText("El proyecto ya está asignado a este estudiante.");
        } else {
            statusLabel.setText("Error al " + context + ".");
        }
        statusLabel.setTextFill(Color.RED);
        logger.error("Error SQL al {}: {}", context, e.getMessage(), e);
    }

    private String createDriveFolders(String idPeriod) {
        try {
            String parentId = null;
            parentId = createOrGetFolder(idPeriod, parentId);
            parentId = createOrGetFolder(student.getNRC(), parentId);
            parentId = createOrGetFolder(student.getTuition(), parentId);
            parentId = createOrGetFolder("Asignacion", parentId);
            return parentId;
        } catch (UnknownHostException e) {
            logger.error("Error de red al crear carpetas en Drive: {}", e.getMessage(), e);
            showAlert("Error de red al crear carpetas en Drive: ");
            return "";
        } catch (SocketTimeoutException e) {
            logger.error("Tiempo de espera agotado al crear carpetas en Drive: {}", e.getMessage(), e);
            showAlert("Tiempo de espera agotado al crear carpetas en Drive: ");
            return "";
        } catch (FileNotFoundException e) {
            logger.error("Archivo no encontrado al crear carpetas en Drive: {}", e.getMessage(), e);
            showAlert("Archivo no encontrado al crear carpetas en Drive: ");
            return "";
        } catch (GoogleJsonResponseException e) {
            logger.error("Error de Google Drive al crear carpetas: {}", e.getDetails().getMessage(), e);
            showAlert("Error de Google Drive al crear carpetas: ");
            return "";
        } catch (GeneralSecurityException e) {
            logger.error("Error de seguridad al crear carpetas en Drive: {}", e.getMessage(), e);
            showAlert("Error de seguridad al crear carpetas en Drive: ");
            return "";
        } catch (IOException e) {
            logger.error("Error entrada/salida al crear carpetas en Drive: {}", e.getMessage(), e);
            showAlert("Error entrada/salida al crear carpetas en Drive: ");
            return "";
        } catch (Exception e) {
            logger.error("Error inesperado al crear carpetas en Drive: {}", e.getMessage(), e);
            showAlert("Error inesperado al crear carpetas en Drive: ");
            return "";
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
            } else if (sqlState != null && sqlState.equals("42S02")) {
                logger.warn("Tabla de grupos no encontrada: {}", e.getMessage(), e);
                showAlert("Tabla de grupos no encontrada en la base de datos: ");
                return "PeriodoDesconocido";
            } else if (sqlState != null && sqlState.equals("42S22")) {
                logger.warn("Columna no encontrada en la tabla de grupos: {}", e.getMessage(), e);
                showAlert("Columna no encontrada en la tabla de grupos: ");
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