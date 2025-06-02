package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import logic.DTO.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GUI_CheckProjectList extends Application {

    private static final Logger logger = LogManager.getLogger(GUI_CheckProjectList.class);
    private Role userRole;

    public void setRole(Role role) {
        this.userRole = role;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckProjectList.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root);

            GUI_CheckProjectListController controller = loader.getController();
            controller.setRole(userRole);

            primaryStage.setTitle("Lista de Proyectos");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            logger.error("Error al iniciar la aplicación: {}", e.getMessage(), e);
            e.printStackTrace();
        }
    }
}