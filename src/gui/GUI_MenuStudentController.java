package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import logic.DTO.StudentDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import logic.services.StudentService;

import java.io.IOException;

public class GUI_MenuStudentController {

    private StudentDTO student;
    private StudentService studentService;

    private static final Logger LOGGER = LogManager.getLogger(GUI_MenuStudentController.class);

    @FXML
    private Label welcomeLabel;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Button logoutButton;

    @FXML
    private Button registerActivityScheduleButton;

    @FXML
    private Button linkActivityToScheduleButton;

    public void setStudentName(String studentName) {
        welcomeLabel.setText("Hola, " + studentName);
    }

    public void setProfileImage() {
        String imageUrl = "https://e7.pngegg.com/pngimages/178/595/png-clipart-user-profile-computer-icons-login-user-avatars-monochrome-black-thumbnail.png";
        try {
            Image image = new Image(imageUrl, true);
            if (image.isError()) {
                LOGGER.error("No se pudo cargar la imagen desde la URL: {}", imageUrl);
                showAlert("Error", "No se pudo cargar la imagen de perfil. Intente más tarde.");
                return;
            }
            profileImageView.setImage(image);
        } catch (IllegalArgumentException e) {
            LOGGER.error("URL de imagen no válida: {}", imageUrl, e);
            showAlert("Error", "La URL de la imagen de perfil no es válida.");
        } catch (Exception e) {
            profileImageView.setImage(null);
            LOGGER.error("Error inesperado al cargar la imagen: {}", e.getMessage(), e);
            showAlert("Error", "Ocurrió un error inesperado al cargar la imagen.");
        }
    }

    @FXML
    private void handleUpdateProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_UpdateProfile.fxml"));
            Parent root = loader.load();

            GUI_UpdateProfileController updateProfileController = loader.getController();

            updateProfileController.setStudentData(
                    student.getNames(),
                    student.getSurnames(),
                    student.getPhone(),
                    student.getEmail()
            );
            updateProfileController.setCurrentStudent(student);
            updateProfileController.setStudentService(studentService);

            Stage stage = new Stage();
            stage.setTitle("Modificar Perfil");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.error("Error al cargar la ventana de actualización de perfil: {}", e.getMessage(), e);
            showAlert("Error", "No se pudo cargar la ventana de actualización de perfil.");
        } catch (Exception e) {
            LOGGER.error("Error al abrir la ventana de actualización de perfil: {}", e.getMessage(), e);
            showAlert("Error", "No se pudo abrir la ventana de actualización de perfil.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Error al interactuar con la interfaz");
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleViewAssignedProject() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_AssignedProject.fxml"));
            Parent root = loader.load();

            GUI_AssignedProjectController controller = loader.getController();
            controller.setStudent(student);

            Stage stage = new Stage();
            stage.setTitle("Proyecto Asignado");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.error("Error al cargar la ventana de proyecto asignado: {}", e.getMessage(), e);
            showAlert("Error", "No se pudo cargar la ventana de proyecto asignado.");
        } catch (Exception e) {
            LOGGER.error("Error al abrir la ventana de proyecto asignado: {}", e.getMessage(), e);
            showAlert("Error", "No se pudo abrir la ventana de proyecto asignado.");
        }
    }

    @FXML
    private void handleRegisterProjectRequest() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_RegisterProjectRequest.fxml"));
            Parent root = loader.load();

            GUI_RegisterProjectRequestController controller = loader.getController();
            controller.setStudent(this.student);

            Stage stage = new Stage();
            stage.setTitle("Registrar Solicitud de Proyecto");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.error("Error al cargar la ventana de registro de solicitud de proyecto: {}", e.getMessage(), e);
            showAlert("Error", "No se pudo cargar la ventana de registro.");
        } catch (Exception e) {
            LOGGER.error("Error al abrir la ventana de registro de solicitud de proyecto: {}", e.getMessage(), e);
            showAlert("Error", "No se pudo abrir la ventana de registro.");
        }
    }

    @FXML
    private void handleRegisterActivitySchedule() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_RegisterActivitySchedule.fxml"));
            Parent root = loader.load();

            GUI_RegisterActivityScheduleController controller = loader.getController();
            controller.setStudent(this.student);

            Stage stage = new Stage();
            stage.setTitle("Registrar Evidencia y Cronograma");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.error("Error al cargar la ventana de registro de evidencia y cronograma: {}", e.getMessage(), e);
            showAlert("Error", "No se pudo cargar la ventana de registro de evidencia y cronograma.");
        } catch (Exception e) {
            LOGGER.error("Error al abrir la ventana de evidencia: {}", e.getMessage(), e);
            showAlert("Error", "No se pudo abrir la ventana de registro de evidencia y cronograma.");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_Login.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Inicio de Sesión");
            stage.show();
        } catch (IOException e) {
            LOGGER.error("Error al cargar la ventana de inicio de sesión: {}", e.getMessage(), e);
            showAlert("Error", "No se pudo cargar la ventana de inicio de sesión.");
        } catch (Exception e) {
            LOGGER.error("Error al cerrar sesión: {}", e.getMessage(), e);
            showAlert("Error", "No se pudo cerrar sesión correctamente.");
        }
    }

    @FXML
    private void handleLinkActivityToSchedule() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_LinkActivityToSchedule.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Vincular Actividad a Cronograma");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.error("Error al cargar la ventana de vinculación: {}", e.getMessage(), e);
            showAlert("Error", "No se pudo cargar la ventana de vinculación.");
        } catch (Exception e) {
            LOGGER.error("Error al abrir la ventana de vinculación: {}", e.getMessage(), e);
            showAlert("Error", "No se pudo abrir la ventana de vinculación.");
        }
    }

    public void setStudent(StudentDTO student) {
        this.student = student;
    }

    public void setStudentService(StudentService studentService) {
        this.studentService = studentService;
    }
}