package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI_RegisterReport extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_RegisterReport.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Registro de Informe");
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();
    }
}