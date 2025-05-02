package gui;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI_MenuStudent extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_MenuStudent.fxml"));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("Menú Estudiante");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
