package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DTO.Role;
import logic.DTO.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GUI_ManageAcademicController {

    private static final Logger logger = LogManager.getLogger(GUI_ManageAcademicController.class);

    @FXML
    private TextField fieldIdUser, fieldState, fieldNumberOffStaff, fieldNames, fieldSurnames, fieldUserName, fieldPassword, fieldRole;

    @FXML
    private Label statusLabel;

    private UserDTO academic;

    public void setAcademicData(UserDTO academic) {
        if (academic == null) {
            logger.error("El objeto UserDTO es nulo.");
            return;
        }

        this.academic = academic;

        fieldIdUser.setText(academic.getIdUser() != null ? academic.getIdUser() : "");
        fieldState.setText(academic.getState() != 0 ? String.valueOf(academic.getState()) : "");
        fieldNumberOffStaff.setText(academic.getNumberOffStaff() != null ? academic.getNumberOffStaff() : "");
        fieldNames.setText(academic.getNames() != null ? academic.getNames() : "");
        fieldSurnames.setText(academic.getSurname() != null ? academic.getSurname() : "");
        fieldUserName.setText(academic.getUserName() != null ? academic.getUserName() : "");
        fieldPassword.setText(academic.getPassword() != null ? academic.getPassword() : "");
        fieldRole.setText(academic.getRole() != null ? academic.getRole().toString() : "");

    }

    @FXML
    private void handleSaveChanges() {
        try {
            if (!areFieldsFilled()) {
                throw new IllegalArgumentException("Todos los campos deben estar llenos.");
            }

            String NumberOffStaff = fieldNumberOffStaff.getText();
            int state = Integer.parseInt(fieldState.getText());
            String names = fieldNames.getText();
            String surnames = fieldSurnames.getText();
            String userName = fieldUserName.getText();
            String password = fieldPassword.getText();
            Role role = Role.valueOf(fieldRole.getText().toUpperCase());

            academic.setState(state);
            academic.setNumberOffStaff(NumberOffStaff);
            academic.setNames(names);
            academic.setSurname(surnames);
            academic.setUserName(userName);
            academic.setPassword(password);
            academic.setRole(role);

            // Aquí se implementaría la lógica para guardar los cambios en la base de datos
            statusLabel.setText("¡Académico actualizado exitosamente!");
            statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
        } catch (Exception e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            logger.error("Error: {}", e.getMessage(), e);
        }
    }

    private boolean areFieldsFilled() {
        return !fieldState.getText().isEmpty() &&
                !fieldNumberOffStaff.getText().isEmpty() &&
                !fieldNames.getText().isEmpty() &&
                !fieldSurnames.getText().isEmpty() &&
                !fieldUserName.getText().isEmpty() &&
                !fieldPassword.getText().isEmpty() &&
                !fieldRole.getText().isEmpty();

    }
}