package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.DTO.ProjectDTO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GUI_ManageProject extends Application {

    private static final Logger logger = LogManager.getLogger(GUI_ManageProject.class);
    private static ProjectDTO project;

    public static void setProject(ProjectDTO projectDTO) {
        project = projectDTO;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_ManageProject.fxml"));
            Parent root = loader.load();

            GUI_ManageProjectController controller = loader.getController();
            controller.setProjectData(project);

            primaryStage.setTitle("Gestión de Proyecto");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            logger.error("Error al iniciar ventana de gestión de proyecto: {}", e.getMessage(), e);
            throw e;
        }
    }
}