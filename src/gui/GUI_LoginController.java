package gui;

import data_access.ConecctionDataBase;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import logic.DTO.StudentDTO;
import logic.DTO.UserDTO;
import logic.exceptions.InvalidCredential;
import logic.services.LoginService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;

public class GUI_LoginController {

    private static final Logger logger = LogManager.getLogger(GUI_LoginController.class);

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Por favor, completa todos los campos.");
            return;
        }

        ConecctionDataBase connectionDB = new ConecctionDataBase();
        try (Connection connection = connectionDB.connectDB()) {
            LoginService loginService = new LoginService();
            Object user = loginService.login(username, password, connection);

            if (user instanceof StudentDTO) {
                StudentDTO student = (StudentDTO) user;
                statusLabel.setText("Bienvenido estudiante, " + student.getNames() + "!");
                statusLabel.setStyle("-fx-text-fill: green;");
            } else if (user instanceof UserDTO) {
                UserDTO generalUser = (UserDTO) user;
                statusLabel.setText("Bienvenido usuario, " + generalUser.getNames() + "!");
                statusLabel.setStyle("-fx-text-fill: green;"); //Solo para hacer commit...
            }
        } catch (InvalidCredential e) {
            logger.warn("Credenciales inválidas: {}", e.getMessage());
            statusLabel.setText(e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage());
            statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
            statusLabel.setStyle("-fx-text-fill: red;");
        } finally {
            connectionDB.closeConnection();
        }
    }
}