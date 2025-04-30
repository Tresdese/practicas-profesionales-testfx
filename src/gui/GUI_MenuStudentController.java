package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GUI_MenuStudentController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private ImageView profileImageView;

    public void setStudentName(String studentName) {
        welcomeLabel.setText("Hola, " + studentName);
    }

    public void setProfileImage() {
        String imageUrl = "https://e7.pngegg.com/pngimages/178/595/png-clipart-user-profile-computer-icons-login-user-avatars-monochrome-black-thumbnail.png";
        Image image = new Image(imageUrl, true); // Carga la imagen desde la URL
        profileImageView.setImage(image);
    }

    @FXML
    private void handleViewCourses() {
        showAlert("Cursos", "Aquí se mostrarían los cursos del estudiante.");
    }

    @FXML
    private void handleViewProgress() {
        showAlert("Avance", "Aquí se mostraría el avance crediticio del estudiante.");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}