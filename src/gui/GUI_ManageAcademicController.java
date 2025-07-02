package gui;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import logic.DAO.UserDAO;
import logic.DTO.Role;
import logic.DTO.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GUI_ManageAcademicController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(GUI_ManageAcademicController.class);

    @FXML
    private TextField numberOfStaffField, namesField, surnamesField;

    @FXML
    private ChoiceBox<Role> roleChoiceBox;

    @FXML
    private Label statusLabel;

    @FXML
    private Button saveButton;

    private UserDTO academic;

    private String originalStaffNumber = "";
    private String originalNames = "";
    private String originalSurnames = "";
    private Role originalRole = null;

    private final ChangeListener<Object> changeListener = (obs, oldVal, newVal) -> checkIfChanged();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeRoleChoiceBox();
        if (saveButton != null) {
            saveButton.setDisable(true);
        }
    }

    private void initializeRoleChoiceBox() {
        roleChoiceBox.setItems(FXCollections.observableArrayList(getVisibleRoles()));
        roleChoiceBox.setConverter(getRoleStringConverter());
    }

    private List<Role> getVisibleRoles() {
        return Arrays.stream(Role.values())
                .filter(role -> role != Role.GUEST)
                .collect(Collectors.toList());
    }

    private StringConverter<Role> getRoleStringConverter() {
        return new StringConverter<Role>() {
            @Override
            public String toString(Role role) {
                return role != null ? role.getDisplayName() : "";
            }
            @Override
            public Role fromString(String string) {
                for (Role role : getVisibleRoles()) {
                    if (role.getDisplayName().equals(string)) {
                        return role;
                    }
                }
                return null;
            }
        };
    }

    private void addFieldListeners() {
        numberOfStaffField.textProperty().addListener(changeListener);
        namesField.textProperty().addListener(changeListener);
        surnamesField.textProperty().addListener(changeListener);
        roleChoiceBox.valueProperty().addListener(changeListener);
    }

    private void removeFieldListeners() {
        numberOfStaffField.textProperty().removeListener(changeListener);
        namesField.textProperty().removeListener(changeListener);
        surnamesField.textProperty().removeListener(changeListener);
        roleChoiceBox.valueProperty().removeListener(changeListener);
    }

    private void setOriginalValues() {
        originalStaffNumber = numberOfStaffField.getText();
        originalNames = namesField.getText();
        originalSurnames = surnamesField.getText();
        originalRole = roleChoiceBox.getValue();
    }

    public void setAcademicData(UserDTO academic) {
        if (academic == null) {
            LOGGER.error("El objeto UserDTO es nulo.");
            return;
        }

        removeFieldListeners();

        this.academic = academic;

        numberOfStaffField.setText(academic.getStaffNumber() != null ? academic.getStaffNumber() : "");
        namesField.setText(academic.getNames() != null ? academic.getNames() : "");
        surnamesField.setText(academic.getSurnames() != null ? academic.getSurnames() : "");
        roleChoiceBox.setValue(academic.getRole() != null ? academic.getRole() : Role.ACADEMIC);

        setOriginalValues();

        if (saveButton != null) {
            saveButton.setDisable(true);
        }

        addFieldListeners();
    }

    private void checkIfChanged() {
        boolean changed =
                !numberOfStaffField.getText().equals(originalStaffNumber) ||
                        !namesField.getText().equals(originalNames) ||
                        !surnamesField.getText().equals(originalSurnames) ||
                        roleChoiceBox.getValue() != originalRole;

        boolean filled = areFieldsFilled();

        if (saveButton != null) {
            saveButton.setDisable(!(changed && filled));
        }
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

                setOriginalValues();
                if (saveButton != null) {
                    saveButton.setDisable(true);
                }
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
}