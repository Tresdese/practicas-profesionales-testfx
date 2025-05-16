package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.DTO.RepresentativeDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class GUI_ManageRepresentative extends Application {

    private static final Logger logger = LogManager.getLogger(GUI_ManageRepresentative.class);
    private static RepresentativeDTO representative;

    public static void setRepresentative(RepresentativeDTO representativeData) {
        representative = representativeData;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            if (representative == null) {
                logger.error("El objeto RepresentativeDTO no puede ser nulo.");
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_ManageRepresentative.fxml"));
            Parent root = loader.load();

            GUI_ManageRepresentativeController controller = loader.getController();
            controller.setRepresentativeData(representative);

            Scene scene = new Scene(root);

            primaryStage.setTitle("Gestionar Representante");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IllegalArgumentException e) {
            logger.error("Error: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("Error al cargar la interfaz GUI_ManageRepresentative.fxml: {}", e.getMessage(), e);
        }
    }
}