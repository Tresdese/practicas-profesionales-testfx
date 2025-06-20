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

public class GUI_AssignProject extends Application {

    private static final Logger logger = LogManager.getLogger(GUI_AssignProject.class);
    private static StudentDTO student;

    public static void setStudent(StudentDTO studentDTO) {
        student = studentDTO;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_AssignProject.fxml"));
            Parent root = loader.load();

            GUI_AssignProjectController controller = loader.getController();
            controller.setStudent(student);

            Scene scene = new Scene(root);

            primaryStage.setTitle("Asignar Proyecto");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            logger.error("Error al cargar la interfaz GUI_AssignProject.fxml: {}", e.getMessage(), e);
        }
    }
}