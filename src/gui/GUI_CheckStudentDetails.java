package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.DTO.StudentDTO;

public class GUI_CheckStudentDetails extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckStudentDetails.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Detalles del Estudiante");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}