package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GUI_MenuStudentController {

    private static final Logger logger = LogManager.getLogger(GUI_MenuStudentController.class);

    @FXML
    private Label welcomeLabel;

    @FXML
    private ImageView profileImageView;

    public void setStudentName(String studentName) {
        welcomeLabel.setText("Hola, " + studentName);
    }

    public void setProfileImage() {
        String imageUrl = "https://e7.pngegg.com/pngimages/178/595/png-clipart-user-profile-computer-icons-login-user-avatars-monochrome-black-thumbnail.png";
        try {
            Image image = new Image(imageUrl, true);
            if (image.isError()) {
                logger.error("No se pudo cargar la imagen desde la URL: {}", imageUrl);
                showAlert("Error", "No se pudo cargar la imagen de perfil. Intente más tarde.");
                return;
            }
            profileImageView.setImage(image);
        } catch (Exception e) {
            profileImageView.setImage(null);
            logger.error("Error inesperado al cargar la imagen: {}", e.getMessage(), e);
            showAlert("Error", "Ocurrió un error inesperado al cargar la imagen.");
        }
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