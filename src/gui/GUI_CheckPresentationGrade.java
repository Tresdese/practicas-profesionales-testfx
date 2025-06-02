package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GUI_CheckPresentationGrade extends Application {

    private static final Logger logger = LogManager.getLogger(GUI_CheckPresentationGrade.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_CheckPresentationGrade.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Consultar Calificaciones de Presentación");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            logger.info("Ventana de Consultar Calificaciones de Presentación iniciada correctamente.");
        } catch (Exception e) {
            logger.error("Error al iniciar la ventana de Consultar Calificaciones de Presentación.", e);
        }
    }
}