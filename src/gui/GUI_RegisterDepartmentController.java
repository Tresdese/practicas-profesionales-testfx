package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import logic.DAO.DepartmentDAO;
import logic.DTO.DepartmentDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class GUI_RegisterDepartmentController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_RegisterDepartmentController.class);

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
        registerButton.setOnAction(event -> {
            try {
                handleRegister();
            } catch (SQLException e) {
                String sqlState = e.getSQLState();
                if ("08001".equals(sqlState)) {
                    LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                    messageLabel.setText("Error de conexión con la base de datos.");
                    messageLabel.setTextFill(Color.RED);
                } else if ("08S01".equals(sqlState)) {
                    LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                    messageLabel.setText("Conexión interrumpida con la base de datos.");
                    messageLabel.setTextFill(Color.RED);
                } else if ("42S02".equals(sqlState)) {
                    LOGGER.error("Tabla o vista no encontrada: {}", e.getMessage(), e);
                    messageLabel.setText("Tabla o vista no encontrada.");
                    messageLabel.setTextFill(Color.RED);
                } else if ("22001".equals(sqlState)) {
                    LOGGER.error("Datos demasiado largos para el campo: {}", e.getMessage(), e);
                    messageLabel.setText("Datos demasiado largos para el campo.");
                    messageLabel.setTextFill(Color.RED);
                } else if ("42S22".equals(sqlState)) {
                    LOGGER.error("Columna no encontrada: {}", e.getMessage(), e);
                    messageLabel.setText("Columna no encontrada.");
                    messageLabel.setTextFill(Color.RED);
                } else if ("42000".equals(sqlState)){
                    LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                    messageLabel.setText("Base de datos desconocida.");
                    messageLabel.setTextFill(Color.RED);
                } else if ("28000".equals(sqlState)) {
                    LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                    messageLabel.setText("Acceso denegado a la base de datos.");
                    messageLabel.setTextFill(Color.RED);
                } else if ("23000".equals(sqlState)) {
                    LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
                    messageLabel.setText("Violación de restricción de integridad.");
                    messageLabel.setTextFill(Color.RED);
                } else if ("HY000".equals(sqlState)) {
                    LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
                    messageLabel.setText("Error general de la base de datos.");
                    messageLabel.setTextFill(Color.RED);
                } else {
                    LOGGER.error("Error de la base de datos al registrar departamento: {}", e);
                    messageLabel.setText("Error de la base de datos al registrar el departamento: ");
                    messageLabel.setTextFill(Color.RED);
                }
            } catch (NullPointerException e) {
                LOGGER.error("Referencia nula al registrar departamento: {}", e.getMessage(), e);
                messageLabel.setText("Error interno al registrar el departamento.");
                messageLabel.setTextFill(Color.RED);
            } catch (IOException e) {
                LOGGER.error("Error al leer el archivo de configuración de la base de datos: {}", e.getMessage(), e);
                messageLabel.setText("Error al leer la configuración de la base de datos.");
                messageLabel.setTextFill(Color.RED);
            } catch (Exception e) {
                LOGGER.error("Error inesperado al registrar departamento: {}", e.getMessage(), e);
                messageLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
                messageLabel.setTextFill(Color.RED);
            }
        });
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
    private void handleRegister() throws SQLException, IOException {
        if (!validateFields()) {
            messageLabel.setText("Todos los campos son obligatorios y deben ser válidos.");
            messageLabel.setTextFill(Color.RED);
            LOGGER.warn("Validación de campos fallida al registrar departamento.");
        } else {
            DepartmentDTO department = createDepartmentDTO();
            if (registerDepartment(department)) {
                messageLabel.setText("Departamento registrado exitosamente.");
                LOGGER.info("Departamento '{}' registrado correctamente.", department.getName());
                nameField.clear();
                descriptionArea.clear();
            } else {
                messageLabel.setText("No se pudo registrar el departamento.");
                LOGGER.error("No se pudo registrar el departamento '{}'.", department.getName());
            }
        }
    }

    private boolean validateFields() {
        return nameField.getText() != null && !nameField.getText().trim().isEmpty()
                && descriptionArea.getText() != null && !descriptionArea.getText().trim().isEmpty()
                && organizationId > 0;
    }

    private DepartmentDTO createDepartmentDTO() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        return new DepartmentDTO(0, name, description, organizationId, 1);
    }

    private boolean registerDepartment(DepartmentDTO department) throws SQLException, IOException {
        DepartmentDAO dao = new DepartmentDAO();
        return dao.insertDepartment(department);
    }
}