package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import logic.services.ServiceConfig;

import java.io.IOException;
import java.sql.SQLException;

public class GUI_CheckListLinkedOrganization extends Application {

    private static final Logger logger = LogManager.getLogger(GUI_CheckListLinkedOrganization.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckListLinkedOrganization.fxml"));
            Parent root = loader.load();

            GUI_CheckListLinkedOrganizationController controller = loader.getController();

            ServiceConfig serviceConfig = new ServiceConfig();
            controller.setOrganizationService(serviceConfig.getLinkedOrganizationService());

            Scene scene = new Scene(root);
            primaryStage.setTitle("Lista de Organizaciones Vinculadas");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (SQLException e) {
            logger.error("Error al conectarse con la base de datos: {}", e.getMessage(), e);
        } catch (IOException e) {
            logger.error("Error al cargar la interfaz GUI_CheckListLinkedOrganization.fxml: {}", e.getMessage(), e);
        }
    }
}