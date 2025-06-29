package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI_Login extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_Login.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Inicio de Sesión");
        primaryStage.show();
    }
}