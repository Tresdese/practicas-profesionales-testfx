package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.DTO.LinkedOrganizationDTO;
import logic.services.ServiceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class GUI_ManageLinkedOrganization extends Application {

    private static final Logger logger = LogManager.getLogger(GUI_ManageLinkedOrganization.class);
    private static LinkedOrganizationDTO linkedOrganization;

    public static void setLinkedOrganization(LinkedOrganizationDTO organizationData) {
        linkedOrganization = organizationData;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            if (linkedOrganization == null) {
                throw new IllegalArgumentException("El objeto LinkedOrganizationDTO no puede ser nulo.");
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_ManageLinkedOrganization.fxml"));
            Parent root = loader.load();

            GUI_ManageLinkedOrganizationController controller = loader.getController();

            ServiceConfig serviceConfig = new ServiceConfig();
            controller.setOrganizationService(serviceConfig.getLinkedOrganizationService());
            controller.setOrganizationData(linkedOrganization);

            Scene scene = new Scene(root);

            primaryStage.setTitle("Gestionar Organización Vinculada");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IllegalArgumentException e) {
            logger.error("Error: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("Error al cargar la interfaz GUI_ManageLinkedOrganization.fxml: {}", e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Error al conectarse con la base de datos: {}", e.getMessage(), e);
        }
    }
}