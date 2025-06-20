package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import logic.DAO.UserDAO;
import logic.DTO.Role;
import logic.DTO.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class GUI_ManageAcademicController implements Initializable {

    private static final Logger logger = LogManager.getLogger(GUI_ManageAcademicController.class);

    @FXML
    private TextField numberOfStaffField, namesField, surnamesField;

    @FXML
    private ChoiceBox<Role> roleBox;

    @FXML
    private Label statusLabel;

    private UserDTO academic;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleBox.setItems(FXCollections.observableArrayList(Role.values()));
    }

    public void setAcademicData(UserDTO academic) {
        if (academic == null) {
            logger.error("El objeto UserDTO es nulo.");
            return;
        }

        this.academic = academic;

        numberOfStaffField.setText(academic.getStaffNumber() != null ? academic.getStaffNumber() : "");
        namesField.setText(academic.getNames() != null ? academic.getNames() : "");
        surnamesField.setText(academic.getSurnames() != null ? academic.getSurnames() : "");
        roleBox.setValue(academic.getRole() != null ? academic.getRole() : Role.ACADEMICO);
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
            Role role = roleBox.getValue();

            academic.setStaffNumber(numberOffStaff);
            academic.setNames(names);
            academic.setSurnames(surnames);
            academic.setRole(role);

            boolean success = userDAO.updateUser(academic);
            if (success) {
                statusLabel.setText("¡Académico actualizado exitosamente!");
                statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            } else {
                throw new Exception("No se pudo actualizar el académico.");
            }
        } catch (Exception e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            logger.error("Error: {}", e.getMessage(), e);
        }
    }

    private boolean areFieldsFilled() {
        return !numberOfStaffField.getText().isEmpty() &&
                !namesField.getText().isEmpty() &&
                !surnamesField.getText().isEmpty() &&
                roleBox.getValue() != null;
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