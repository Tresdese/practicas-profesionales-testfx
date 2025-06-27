package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import logic.DAO.UserDAO;
import logic.DTO.Role;
import logic.DTO.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class GUI_ManageAcademicController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(GUI_ManageAcademicController.class);

    @FXML
    private TextField numberOfStaffField, namesField, surnamesField;

    @FXML
    private ChoiceBox<Role> roleChoiceBox;

    @FXML
    private Label statusLabel;

    private UserDTO academic;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleChoiceBox.setItems(FXCollections.observableArrayList(Role.values()));
    }

    public void setAcademicData(UserDTO academic) {
        if (academic == null) {
            LOGGER.error("El objeto UserDTO es nulo.");
            return;
        }

        this.academic = academic;

        numberOfStaffField.setText(academic.getStaffNumber() != null ? academic.getStaffNumber() : "");
        namesField.setText(academic.getNames() != null ? academic.getNames() : "");
        surnamesField.setText(academic.getSurnames() != null ? academic.getSurnames() : "");
        roleChoiceBox.setValue(academic.getRole() != null ? academic.getRole() : Role.ACADEMICO);
    }

    @FXML
    private void handleSaveChanges() {
        try {
            UserDAO userDAO = new UserDAO();

            if (!areFieldsFilled()) {
                throw new IllegalArgumentException("Todos los campos deben estar llenos.");
            }

            String numberOffStaff = numberOfStaffField.getText();
            String names = namesField.getText();
            String surnames = surnamesField.getText();
            Role role = roleChoiceBox.getValue();

            academic.setStaffNumber(numberOffStaff);
            academic.setNames(names);
            academic.setSurnames(surnames);
            academic.setRole(role);

            boolean success = userDAO.updateUser(academic);
            if (success) {
                statusLabel.setText("¡Académico actualizado exitosamente!");
                statusLabel.setTextFill(Color.GREEN);
            } else {
                throw new Exception("No se pudo actualizar el académico.");
            }
        } catch (IllegalArgumentException e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error: {}", e.getMessage(), e);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al actualizar el académico.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al actualizar el académico: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al actualizar el académico.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error: {}", e.getMessage(), e);
        }
    }

    private boolean areFieldsFilled() {
        return !numberOfStaffField.getText().isEmpty() &&
                !namesField.getText().isEmpty() &&
                !surnamesField.getText().isEmpty() &&
                roleChoiceBox.getValue() != null;
    }

    private Role getRoleFromText(String text) {
        switch (text) {
            case "Académico":
                return Role.ACADEMICO;
            case "Académico Evaluador":
                return Role.ACADEMICO_EVALUADOR;
            case "Coordinador":
                return Role.COORDINADOR;
            default:
                throw new IllegalArgumentException("Rol no válido: " + text);
        }
    }
}