package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GUI_MenuUserController {

    private static final Logger logger = LogManager.getLogger(GUI_MenuUserController.class);

    @FXML
    private Label welcomeLabel;

    public void setUserName(String userName) {
        welcomeLabel.setText("Hola, " + userName);
    }

    @FXML
    private void handleViewStudentList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_CheckListOfStudents.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Lista de Estudiantes");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de lista de estudiantes: {}", e.getMessage(), e);
        }
    }
}