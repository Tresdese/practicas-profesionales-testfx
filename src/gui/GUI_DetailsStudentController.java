package gui;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import logic.DTO.StudentDTO;
import javafx.scene.control.Button;
import logic.DTO.StudentProjectDTO;
import logic.DTO.ProjectDTO;
import logic.DTO.LinkedOrganizationDTO;
import logic.DAO.StudentProjectDAO;
import logic.DAO.ProjectDAO;
import logic.DAO.LinkedOrganizationDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class GUI_DetailsStudentController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_DetailsStudentController.class);

    @FXML
    private Label tuitionLabel;
    @FXML
    private Label namesLabel;
    @FXML
    private Label surnamesLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label NRCLabel;
    @FXML
    private Label projectNameLabel;
    @FXML
    private Label projectDescriptionLabel;
    @FXML
    private Label projectOrganizationLabel;
    @FXML
    private Button checkSelfAssessmentButton;

    private HostServices hostServices;

    public void setStudent(StudentDTO student) {
        if (student != null) {
            tuitionLabel.setText(student.getTuition());
            namesLabel.setText(student.getNames());
            surnamesLabel.setText(student.getSurnames());
            emailLabel.setText(student.getEmail());
            NRCLabel.setText(student.getNRC());

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
                    projectNameLabel.setText(project.getName());
                    projectDescriptionLabel.setText(project.getDescription());

                    LinkedOrganizationDAO orgDAO = new LinkedOrganizationDAO();
                    LinkedOrganizationDTO org = orgDAO.searchLinkedOrganizationById(String.valueOf(project.getIdOrganization()));
                    projectOrganizationLabel.setText(org != null ? org.getName() : "N/A");
                } else {
                    projectNameLabel.setText("No asignado");
                    projectDescriptionLabel.setText("-");
                    projectOrganizationLabel.setText("-");
                }
            } else {
                projectNameLabel.setText("No asignado");
                projectDescriptionLabel.setText("-");
                projectOrganizationLabel.setText("-");
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
             if (sqlState != null && sqlState.equals("08001")) {
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                projectNameLabel.setText("Error de conexion con la base de datos");
                projectDescriptionLabel.setText("Error de error de conexion con la base de datos");
                projectOrganizationLabel.setText("Error de error de conexion con la base de datos");
                showAlert("Error de conexión", "No se pudo conectar a la base de datos. Por favor, intente más tarde.");
            } else if (sqlState != null && sqlState.equals("08S01")) {
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                projectNameLabel.setText("Error de conexion interrumpida con la base de datos");
                projectDescriptionLabel.setText("Error de conexion interrumpida con la base de datos");
                projectOrganizationLabel.setText("Error de conexion interrumpida con la base de datos");
                showAlert("Conexión interrumpida", "La conexión con la base de datos se ha interrumpido. Por favor, intente más tarde.");
             } else if (sqlState != null && sqlState.equals("42S02")) {
                LOGGER.error("Tabla no encontrada en la base de datos: {}", e.getMessage(), e);
                projectNameLabel.setText("Error de tabla no encontrada");
                projectDescriptionLabel.setText("Error de tabla no encontrada");
                projectOrganizationLabel.setText("Error de tabla no encontrada");
                showAlert("Tabla no encontrada", "La tabla solicitada no se encuentra disponible. Por favor, intente más tarde.");
            } else if (sqlState != null && sqlState.equals("42S22")) {
                LOGGER.error("Columna no encontrada en la base de datos: {}", e.getMessage(), e);
                projectNameLabel.setText("Error de columna no encontrada");
                projectDescriptionLabel.setText("Error de columna no encontrada");
                projectOrganizationLabel.setText("Error de columna no encontrada");
                showAlert("Columna no encontrada", "La columna solicitada no se encuentra disponible. Por favor, intente más tarde.");
             } else if (sqlState != null && sqlState.equals("HY000")) {
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
                projectNameLabel.setText("Error general de la base de datos");
                projectDescriptionLabel.setText("Error general de la base de datos");
                projectOrganizationLabel.setText("Error general de la base de datos");
                showAlert("Error general", "Ocurrió un error general en la base de datos. Por favor, intente más tarde.");
             } else if (sqlState != null && sqlState.equals("42000")) {
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                projectNameLabel.setText("Error de base de datos desconocida");
                projectDescriptionLabel.setText("Error de base de datos desconocida");
                projectOrganizationLabel.setText("Error de base de datos desconocida");
                showAlert("Base de datos desconocida", "La base de datos solicitada no se encuentra disponible. Por favor, intente más tarde.");
            } else if (sqlState != null && sqlState.equals("28000")) {
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                projectNameLabel.setText("Error de acceso denegado a la base de datos");
                projectDescriptionLabel.setText("Error de acceso denegado a la base de datos");
                projectOrganizationLabel.setText("Error de acceso denegado a la base de datos");
                showAlert("Acceso denegado", "No tiene permiso para acceder a la base de datos. Por favor, contacte al administrador.");
             } else {
                LOGGER.error("Error al obtener el proyecto asignado: {}", e.getMessage(), e);
                projectNameLabel.setText("Error de base de datos al obtener el proyecto");
                projectDescriptionLabel.setText("Error al obtener el proyecto");
                projectOrganizationLabel.setText("Error al obtener el proyecto");
                showAlert("Error al obtener el proyecto", "Ocurrió un error al intentar obtener el proyecto asignado. Por favor, intente más tarde.");
             }
        } catch (NullPointerException e) {
            LOGGER.error("Referencia nula al obtener el proyecto asignado: {}", e.getMessage(), e);
            projectNameLabel.setText("Error de referencia nula");
            projectDescriptionLabel.setText("Error de referencia nula");
            projectOrganizationLabel.setText("Error de referencia nula");
            showAlert("Error de referencia nula", "Ocurrió un error interno al intentar obtener el proyecto asignado. Por favor, intente más tarde.");
        } catch (IOException e) {
            LOGGER.error("Error al leer el archivo de configuración de la base de datos: {}", e.getMessage(), e);
            projectNameLabel.setText("Error de lectura de configuración");
            projectDescriptionLabel.setText("Error de lectura de configuración");
            projectOrganizationLabel.setText("Error de lectura de configuración");
            showAlert("Error de configuración", "No se pudo leer la configuración de la base de datos. Por favor, intente más tarde.");
        }
        catch (Exception e) {
            LOGGER.error("Error inesperado al obtener el proyecto asignado: {}", e.getMessage(), e);
            projectNameLabel.setText("Error inesperado");
            projectDescriptionLabel.setText("Error inesperado");
            projectOrganizationLabel.setText("Error inesperado");
            showAlert("Error inesperado", "Ocurrió un error inesperado al intentar obtener el proyecto asignado. Por favor, intente más tarde.");
        }
    }

    @FXML
    private void handleCheckReports() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckListOfReports.fxml"));
            Parent root = loader.load();

            GUI_CheckListOfReportsController controller = loader.getController();

            controller.setStudentTuition(tuitionLabel.getText());

            Stage stage = new Stage();
            stage.setTitle("Lista de Reportes");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.error("No se pudo leer el fxml al abrir la ventana de reportes: {}", e.getMessage(), e);
            showAlert("Error", "No se pudo leer el fxml al abrir la ventana de reportes. Por favor, intente más tarde.");
        } catch (Exception e) {
            LOGGER.error("Error inesperado al abrir la ventana de reportes: {}", e.getMessage(), e);
            showAlert("Error", "Ocurrió un error inesperado al abrir la ventana de reportes. Por favor, intente más tarde.");
        }
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @FXML
    private void handleCheckSelfAssessment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckSelfAssessment.fxml"));
            Parent root = loader.load();
            GUI_CheckSelfAssessmentController controller = loader.getController();
            controller.setStudentTuition(tuitionLabel.getText());

            Stage stage = new Stage();
            stage.setTitle("Autoevaluación");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.error("No se pudo leer el fxml al abrir la ventana de autoevaluación: {}", e.getMessage(), e);
            showAlert("Error", "No se pudo leer el fxml al abrir la ventana de autoevaluación. Por favor, intente más tarde.");
        } catch (Exception e) {
            LOGGER.error("Error inesperado al abrir la ventana de autoevaluación: {}", e.getMessage(), e);
            showAlert("Error", "Ocurrió un error inesperado al abrir la ventana de autoevaluación. Por favor, intente más tarde.");
        }
    }

    @FXML
    private void handleCheckPresentationGrade() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckPresentationGrade.fxml"));
            Parent root = loader.load();

            GUI_CheckPresentationGradeController controller = loader.getController();
            StudentDTO student = new StudentDTO();
            student.setTuition(tuitionLabel.getText());
            student.setNames(namesLabel.getText());
            student.setSurnames(surnamesLabel.getText());
            student.setEmail(emailLabel.getText());
            student.setNRC(NRCLabel.getText());
            controller.setStudent(student);

            Stage stage = new Stage();
            stage.setTitle("Calificaciones de Presentación");
            stage.setScene(new Scene(root));
            stage.show();
        }catch (IOException e) {
            LOGGER.error("No se pudo leer el fxml al abrir la ventana de Calificaciones de Presentacion: {}", e.getMessage(), e);
            showAlert("Error", "No se pudo leer el fxml al abrir la ventana de Calificaciones de Presentacion. Por favor, intente más tarde.");
        } catch (Exception e) {
            LOGGER.error("Error inesperado al abrir la ventana de Calificaciones de Presentacion: {}", e.getMessage(), e);
            showAlert("Error", "Ocurrió un error inesperado al abrir la ventana de Calificaciones de Presentacion. Por favor, intente más tarde.");
        }
    }

    @FXML
    private void handleCheckScheduleOfActivities() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckScheduleOfActivities.fxml"));
            Parent root = loader.load();

            GUI_CheckScheduleOfActivitiesController controller = loader.getController();
            controller.setStudentTuition(tuitionLabel.getText());

            Stage stage = new Stage();
            stage.setTitle("Cronograma de Actividades");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.error("No se pudo leer el fxml al abrir el cronograma: {}", e.getMessage(), e);
            showAlert("Error", "No se pudo leer el FXML del cronograma. Por favor, intente más tarde.");
        } catch (Exception e) {
            LOGGER.error("Error inesperado al abrir el cronograma: {}", e.getMessage(), e);
            showAlert("Error", "Ocurrió un error inesperado al abrir el cronograma. Por favor, intente más tarde.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}