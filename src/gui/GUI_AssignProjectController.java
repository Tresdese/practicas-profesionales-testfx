package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import logic.DAO.StudentProjectDAO;
import logic.DTO.ProjectDTO;
import logic.DTO.StudentDTO;
import logic.DTO.StudentProjectDTO;
import logic.services.ProjectService;
import logic.services.ServiceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

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
        } catch (RuntimeException e) {
            logger.error("Error al inicializar servicios: {}", e.getMessage(), e);
            statusLabel.setText("Error interno. Intente más tarde.");
            buttonAssignProject.setDisable(true);
        } catch (SQLException e) {
            logger.error("Error al conectar a la base de datos: {}", e.getMessage(), e);
            statusLabel.setText("Error de conexión a la base de datos.");
            buttonAssignProject.setDisable(true);
        }

        projectChoiceBox.setConverter(new javafx.util.StringConverter<ProjectDTO>() {
            @Override
            public String toString(ProjectDTO project) {
                return project != null ? project.getName() : "";
            }

            @Override
            public ProjectDTO fromString(String string) {
                return null;
            }
        });

        loadProjects();
    }

    public void setStudent(StudentDTO student) {
        this.student = student;
        if (student != null) {
            studentNameLabel.setText(student.getNames() + " " + student.getSurnames());
        }
    }

    private void loadProjects() {
        try {
            List<ProjectDTO> availableProjects = projectService.getAllProjects();
            ObservableList<ProjectDTO> projectList = FXCollections.observableArrayList(availableProjects);
            projectChoiceBox.setItems(projectList);

            if (!projectList.isEmpty()) {
                projectChoiceBox.setValue(projectList.get(0));
            } else {
                statusLabel.setText("No hay proyectos disponibles para asignar");
                buttonAssignProject.setDisable(true);
            }
        } catch (SQLException e) {
            statusLabel.setText("Error al cargar los proyectos disponibles");
            logger.error("Error al cargar los proyectos disponibles: {}", e.getMessage(), e);
            buttonAssignProject.setDisable(true);
        }
    }

    @FXML
    private void handleAssignProject() {
        if (student == null) {
            statusLabel.setText("No se ha seleccionado un estudiante");
            return;
        }

        ProjectDTO selectedProject = projectChoiceBox.getValue();
        if (selectedProject == null) {
            statusLabel.setText("Debe seleccionar un proyecto");
            return;
        }

        try {
            StudentProjectDTO studentProject = new StudentProjectDTO(selectedProject.getIdProject(), student.getTuiton());

            boolean success = studentProjectDAO.insertStudentProject(studentProject);

            if (success) {
                statusLabel.setText("Proyecto asignado exitosamente");
                statusLabel.setStyle("-fx-text-fill: green;");

                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        javafx.application.Platform.runLater(this::closeWindow);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            } else {
                statusLabel.setText("No se pudo asignar el proyecto");
            }
        } catch (SQLException e) {
            statusLabel.setText("Error al asignar el proyecto");
            logger.error("Error al asignar el proyecto: {}", e.getMessage(), e);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) buttonAssignProject.getScene().getWindow();
        stage.close();
    }
}