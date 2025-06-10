package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI_DetailsStudent extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_DetailsStudent.fxml"));
        Parent root = loader.load();
        GUI_DetailsStudentController controller = loader.getController();
        controller.setHostServices(getHostServices());
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
