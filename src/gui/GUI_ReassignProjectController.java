package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import logic.DAO.ProjectDAO;
import logic.DAO.StudentProjectDAO;
import logic.DTO.StudentProjectDTO;
import logic.DTO.ProjectDTO;
import logic.DTO.StudentDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GUI_ReassignProjectController {
    private static final Logger LOGGER = LogManager.getLogger(GUI_ReassignProjectController.class);

    @FXML
    private Label studentNameLabel;
    
    @FXML
    private ChoiceBox<ProjectDTO> projectChoiceBox;

    @FXML
    private Button assignProjectButton;

    @FXML
    private Label statusLabel;

    private StudentDTO student;
    private ProjectDTO currentProject;

    @FXML
    private void initialize() {
        assignProjectButton.setOnAction(event -> handleAssignProjectButton());
    }

    @FXML
    public void handleAssignProjectButton() {
        ProjectDTO selectedProject = projectChoiceBox.getValue();
        if (selectedProject == null) {
            statusLabel.setText("Seleccione un proyecto.");
            return;
        }
        try {
            StudentProjectDAO studentProjectDAO = new StudentProjectDAO();
            StudentProjectDTO studentProjectDTO = new StudentProjectDTO(selectedProject.getIdProject(), student.getTuition());
            studentProjectDAO.updateStudentProject(studentProjectDTO);
            statusLabel.setText("Proyecto reasignado correctamente.");
            closeWindow();
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                statusLabel.setText("Columna desconocida en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna desconocida en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                statusLabel.setText("Tabla desconocida en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla desconocida en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("HY000")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("23000")) {
                statusLabel.setText("El proyecto ya está asignado a este estudiante.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("El proyecto ya está asignado a este estudiante: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al reasignar el proyecto.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al reasignar el proyecto: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al leer el archivo de configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al leer el archivo de configuración de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error al reasignar el proyecto.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al reasignar el proyecto: {}", e.getMessage(), e);
        }
    }

    public void setProjectStudent(StudentDTO student, ProjectDTO currentProject) {
        this.student = student;
        this.currentProject = currentProject;
        studentNameLabel.setText(student.getNames() + " " + student.getSurnames());
        loadAvailableProjects();
        if (currentProject != null) {
            projectChoiceBox.setValue(currentProject);
        }
    }

    private void loadAvailableProjects() {
        try {
            ProjectDAO projectDAO = new ProjectDAO();
            List<ProjectDTO> allProjects = projectDAO.getAllProjects();
            ObservableList<ProjectDTO> projectList = FXCollections.observableArrayList(allProjects);
            projectChoiceBox.setItems(projectList);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                statusLabel.setText("Columna desconocida en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna desconocida en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                statusLabel.setText("Tabla desconocida en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla desconocida en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("HY000")) {
                statusLabel.setText("Error general de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar proyectos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al cargar proyectos: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al leer el archivo de configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al leer el archivo de configuración de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error al cargar proyectos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al cargar proyectos: {}", e.getMessage(), e);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) assignProjectButton.getScene().getWindow();
        stage.close();
    }
}