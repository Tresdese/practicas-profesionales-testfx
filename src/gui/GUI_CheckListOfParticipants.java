package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI_CheckListOfParticipants extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckListOfParticipants.fxml"));
        Parent root = loader.load();

        GUI_CheckListOfParticipantsController controller = loader.getController();
        controller.setPresentationId(1);

        primaryStage.setTitle("Lista de Participantes");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}