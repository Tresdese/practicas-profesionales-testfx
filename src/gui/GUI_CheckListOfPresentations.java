package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class GUI_CheckListOfPresentations extends Application {

    private static final Logger logger = LogManager.getLogger(GUI_CheckListOfPresentations.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_CheckListOfPresentations.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Lista de Presentaciones");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            logger.info("Ventana de Lista de Presentaciones iniciada correctamente.");
        } catch (IOException e) {
            logger.error("Error al iniciar la ventana de Lista de Presentaciones.", e);
        }
    }
}