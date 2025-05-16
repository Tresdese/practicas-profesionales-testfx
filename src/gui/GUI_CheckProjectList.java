package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GUI_CheckProjectList extends Application {

    private static final Logger logger = LogManager.getLogger(GUI_CheckProjectList.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gui/GUI_CheckProjectList.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Lista de Proyectos");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            logger.error("Error al iniciar la aplicación: {}", e.getMessage(), e);
            e.printStackTrace();
        }
    }
}