package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DAO.DepartmentDAO;
import logic.DTO.DepartmentDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class GUI_RegisterDepartmentController {

    private static final Logger logger = LogManager.getLogger(GUI_RegisterDepartmentController.class);

    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Button registerButton;
    @FXML
    private Label messageLabel, nameCharCountLabel, descriptionCharCountLabel;

    private static final int MAX_NAME = 100;
    private static final int MAX_DESCRIPTION = 200;

    private int organizationId = -1;

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    @FXML
    private void initialize() {
        configureTextFormatters();
        configureCharCountLabels();
    }

    private TextFormatter<String> createTextFormatter(int maxLength) {
        return new TextFormatter<>(change ->
                change.getControlNewText().length() <= maxLength ? change : null
        );
    }

    private void configureTextFormatters() {
        nameField.setTextFormatter(createTextFormatter(MAX_NAME));
        descriptionArea.setTextFormatter(createTextFormatter(MAX_DESCRIPTION));
    }

    private void configureCharCountLabels() {
        nameCharCountLabel.setText("0/" + MAX_NAME);
        descriptionCharCountLabel.setText("0/" + MAX_DESCRIPTION);

        nameField.textProperty().addListener((observable, oldValue, newValue) ->
                nameCharCountLabel.setText(newValue.length() + "/" + MAX_NAME));

        descriptionArea.textProperty().addListener((observable, oldValue, newValue) ->
                descriptionCharCountLabel.setText(newValue.length() + "/" + MAX_DESCRIPTION));
    }



    @FXML
    private void handleRegister() {
        if (!validateFields()) {
            return;
        }
        DepartmentDTO department = createDepartmentDTO();
        try {
            if (registerDepartment(department)) {
                messageLabel.setText("Departamento registrado exitosamente.");
                logger.info("Departamento '{}' registrado correctamente.", department.getName());
                nameField.clear();
                descriptionArea.clear();
            } else {
                messageLabel.setText("No se pudo registrar el departamento.");
                logger.error("No se pudo registrar el departamento '{}'.", department.getName());
            }
        } catch (SQLException e) {
            messageLabel.setText("Error de base de datos: " + e.getMessage());
            logger.error("Error SQL al registrar departamento: {}", e.getMessage(), e);
        } catch (Exception e) {
            messageLabel.setText("Error al registrar: " + e.getMessage());
            logger.error("Error inesperado al registrar departamento: {}", e.getMessage(), e);
        }
    }

    private boolean validateFields() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (name.isEmpty() || description.isEmpty()) {
            messageLabel.setText("Todos los campos son obligatorios.");
            logger.warn("Campos vacíos al intentar registrar departamento.");
            return false;
        }
        if (organizationId <= 0) {
            messageLabel.setText("ID de organización no válido.");
            logger.error("ID de organización no válido: {}", organizationId);
            return false;
        }
        return true;
    }

    private DepartmentDTO createDepartmentDTO() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        return new DepartmentDTO(0, name, description, organizationId);
    }

    private boolean registerDepartment(DepartmentDTO department) throws SQLException {
        DepartmentDAO dao = new DepartmentDAO();
        return dao.insertDepartment(department);
    }
}