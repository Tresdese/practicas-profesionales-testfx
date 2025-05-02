package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class GUI_RegisterLinkedOrganization extends Application {

    private static final Logger logger = LogManager.getLogger(GUI_RegisterLinkedOrganization.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterLinkedOrganization.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            primaryStage.setTitle("Registrar Organizacion");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            logger.error("Error al cargar la interfaz GUI_RegisterLinkedOrganization.fxml: {}", e.getMessage(), e);
        }
    }
}
