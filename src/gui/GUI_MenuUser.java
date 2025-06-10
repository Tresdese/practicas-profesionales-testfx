package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GUI_MenuUser extends Application {

    private static final Logger logger = LogManager.getLogger(GUI_MenuUser.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_MenuUser.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Menú de Usuario");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            logger.info("Ventana de Menú de Usuario iniciada correctamente.");
        } catch (Exception e) {
            logger.error("Error al iniciar la ventana de Menú de Usuario.", e);
        }
    }
}