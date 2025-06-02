package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.DTO.ProjectDTO;
import logic.DTO.StudentDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class GUI_ReassignProject extends Application {

    private static final Logger logger = LogManager.getLogger(GUI_ReassignProject.class);
    private static StudentDTO student;
    private static ProjectDTO currentProject;

    public static void setProjectStudent(StudentDTO studentDTO, ProjectDTO projectDTO) {
        student = studentDTO;
        currentProject = projectDTO;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_ReassignProject.fxml"));
            Parent root = loader.load();

            GUI_ReassignProjectController controller = loader.getController();
            controller.setProjectStudent(student, currentProject);

            Scene scene = new Scene(root);

            primaryStage.setTitle("Reasignar Proyecto");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            logger.error("Error al cargar la interfaz GUI_ReassignProject.fxml: {}", e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}