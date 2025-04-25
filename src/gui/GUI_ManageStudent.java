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

public class GUI_ManageStudent extends Application {

    private static final Logger logger = LogManager.getLogger(GUI_ManageStudent.class);
    private static StudentDTO student;

    public static void setStudent(StudentDTO studentData) {
        student = studentData;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            if (student == null) {
                throw new IllegalArgumentException("El objeto StudentDTO no puede ser nulo.");
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_ManageStudent.fxml"));
            Parent root = loader.load();

            // Obtener el controlador y pasar los datos del estudiante
            GUI_ManageStudentController controller = loader.getController();
            controller.setStudentData(student);

            Scene scene = new Scene(root);

            primaryStage.setTitle("Gestionar Estudiante");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IllegalArgumentException e) {
            logger.error("Error: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("Error al cargar la interfaz GUI_ManageStudent.fxml: {}", e.getMessage(), e);
        }
    }
}