package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class GUI_ManageActivity extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("GUI_ManageActivity.fxml"));
        primaryStage.setTitle("Gestión de Actividades");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}