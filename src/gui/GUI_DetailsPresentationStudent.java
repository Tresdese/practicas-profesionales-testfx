package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI_DetailsPresentationStudent extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_DetailsPresentationStudent.fxml"));
            Parent root = loader.load();

            primaryStage.setTitle("Detalles de Presentación del Estudiante");
            primaryStage.setScene(new Scene(root));

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
