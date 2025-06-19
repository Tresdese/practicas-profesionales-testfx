package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.DTO.StudentDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class GUI_RegisterProjectRequest extends Application {

    private static final Logger logger = LogManager.getLogger(GUI_RegisterProjectRequest.class);

    private static StudentDTO estudianteActual;

    public static void setEstudianteActual(StudentDTO estudiante) {
        estudianteActual = estudiante;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterProjectRequest.fxml"));
            Parent root = loader.load();
            GUI_RegisterProjectRequestController controller = loader.getController();
            controller.setStudent(estudianteActual);

            Scene scene = new Scene(root);

            primaryStage.setTitle("Registrar Solicitud de Proyecto");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            logger.error("Error al cargar la interfaz GUI_RegisterProjectRequest.fxml: {}", e.getMessage(), e);
        }
    }
}