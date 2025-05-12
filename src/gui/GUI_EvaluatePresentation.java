package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GUI_EvaluatePresentation extends Application {

    private static final Logger logger = LogManager.getLogger(GUI_EvaluatePresentation.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_EvaluatePresentation.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Calificar Presentación");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            logger.info("Ventana de Calificar Presentación iniciada correctamente.");
        } catch (Exception e) {
            logger.error("Error al iniciar la ventana de Calificar Presentación.", e);
        }
    }
}