package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.DTO.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class GUI_CheckProjectRequestList extends Application {

    private static final Logger logger = LogManager.getLogger(GUI_CheckProjectRequestList.class);
    private Role userRole;

    public void setUserRole(Role role) {
        this.userRole = role;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckProjectRequestList.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            GUI_CheckProjectRequestListController controller = loader.getController();
            controller.setUserRole(userRole);

            primaryStage.setTitle("Solicitudes de Proyectos");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            logger.error("Error al cargar la interfaz GUI_CheckProjectRequestList.fxml: {}", e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}