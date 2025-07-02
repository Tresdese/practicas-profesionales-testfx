package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI_DeleteDepartment extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_DeleteDepartment.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Eliminar Departamento");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
