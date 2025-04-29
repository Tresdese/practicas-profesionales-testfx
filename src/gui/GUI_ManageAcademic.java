package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.DTO.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class GUI_ManageAcademic extends Application {

    private static final Logger logger = LogManager.getLogger(GUI_ManageAcademic.class);
    private static UserDTO academic;

    public static void setAcademic(UserDTO academicData) {
        academic = academicData;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            if (academic == null) {
                throw new IllegalArgumentException("El objeto UserDTO no puede ser nulo.");
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_ManageAcademic.fxml"));
            Parent root = loader.load();

            GUI_ManageAcademicController controller = loader.getController();
            controller.setAcademicData(academic);

            Scene scene = new Scene(root);

            primaryStage.setTitle("Gestionar Academico");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IllegalArgumentException e) {
            logger.error("Error: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("Error al cargar la interfaz GUI_ManageAcademic.fxml: {}", e.getMessage(), e);
        }
    }
}