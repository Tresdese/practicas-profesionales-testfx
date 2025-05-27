package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GUI_AssignedProject extends Application {

    private static final Logger logger = LogManager.getLogger(GUI_AssignedProject.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_AssignedProject.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("Proyecto Asignado");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            logger.error("Error al iniciar la aplicación: {}", e.getMessage(), e);
            e.printStackTrace();
        }
    }
}