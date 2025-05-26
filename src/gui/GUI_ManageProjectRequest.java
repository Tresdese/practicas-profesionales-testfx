package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.DAO.ProjectRequestDAO;
import logic.DTO.ProjectRequestDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class GUI_ManageProjectRequest extends Application {

    private static final Logger logger = LogManager.getLogger(GUI_ManageProjectRequest.class);
    private static ProjectRequestDTO projectRequest;

    public static void setProjectRequest(ProjectRequestDTO requestData) {
        projectRequest = requestData;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            if (projectRequest == null) {
                throw new IllegalArgumentException("El objeto ProjectRequestDTO no puede ser nulo.");
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_ManageProjectRequest.fxml"));
            Parent root = loader.load();

            GUI_ManageProjectRequestController controller = loader.getController();

            ProjectRequestDAO projectRequestDAO = new ProjectRequestDAO();
            controller.setProjectRequestDAO(projectRequestDAO);
            controller.setProjectRequestData(projectRequest);

            Scene scene = new Scene(root);

            primaryStage.setTitle("Gestionar Solicitud de Proyecto");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IllegalArgumentException e) {
            logger.error("Error: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("Error al cargar la interfaz GUI_ManageProjectRequest.fxml: {}", e.getMessage(), e);
        }
    }
}