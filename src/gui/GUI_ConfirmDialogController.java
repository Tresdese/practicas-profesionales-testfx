package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class GUI_ConfirmDialogController {

    @FXML
    private Label confirmMessageLabel, informationMessageLabel;

    private boolean confirmed = false;

    public void setInformationMessage(String message) {
        informationMessageLabel.setText(message);
    }

    public void setConfirmMessage(String message) {
        confirmMessageLabel.setText(message);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    @FXML
    private void handleOk() {
        confirmed = true;
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        confirmed = false;
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) confirmMessageLabel.getScene().getWindow()).close();
    }
}
