package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.DAO.DepartmentDAO;
import logic.DTO.DepartmentDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class GUI_DeleteDepartmentController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_DeleteDepartmentController.class);

    @FXML
    private ChoiceBox<DepartmentDTO> departmentsChoiceBox;

    @FXML
    private Button cancelButton, deleteButton;

    @FXML
    private Label statusLabel;

    private String organizationId;

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
        loadDepartments();
    }

    @FXML
    public void initialize() {
        cancelButton.setOnAction(event -> handleCancel());
        deleteButton.setOnAction(event -> handleDelete());
    }

    private void loadDepartments() {
        try {
            DepartmentDAO departmentDAO = new DepartmentDAO();
            List<DepartmentDTO> allDepartments = departmentDAO.getAllDepartmentsByOrganizationId(Integer.parseInt(organizationId));
            List<DepartmentDTO> activeDepartments = allDepartments.stream()
                    .filter(dept -> dept.getStatus() == 1)
                    .collect(Collectors.toList());
            departmentsChoiceBox.getItems().setAll(activeDepartments);
            if (activeDepartments.isEmpty()) {
                statusLabel.setText("No hay departamentos activos.");
                statusLabel.setTextFill(Color.BLACK);
            } else {
                statusLabel.setText("");
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Error de interrupcion de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de interrupcion de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                statusLabel.setText("Tabla de departamentos no encontrada. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de departamentos no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                statusLabel.setText("Columna de departamentos no encontrada. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna de departamentos no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("HY000")) {
                statusLabel.setText("Error general de la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar departamentos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al cargar departamentos: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("No se pudo leer el archivo de configuracion de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error de E/S al cargar departamentos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Ocurrió un error inesperado al cargar departamentos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al cargar departamentos: {}", e.getMessage(), e);
        }
    }

    private void handleCancel() {
        cancelButton.getScene().getWindow().hide();
    }

    private void handleDelete() {
        DepartmentDTO selected = departmentsChoiceBox.getValue();
        if (selected == null) {
            statusLabel.setText("Seleccione un departamento.");
            statusLabel.setTextFill(Color.RED);
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_ConfirmDialog.fxml"));
                Parent root = loader.load();
                GUI_ConfirmDialogController confirmController = loader.getController();
                confirmController.setInformationMessage("Al borrar un departamento, se eliminarán los datos asociados a este.");
                confirmController.setConfirmMessage("¿Está seguro de que desea eliminar el departamento " + selected.getName() + "?");
                Stage confirmStage = new Stage();
                confirmStage.setTitle("Confirmar Eliminación");
                confirmStage.setScene(new Scene(root));
                confirmStage.showAndWait();
                if (confirmController.isConfirmed()) {
                    DepartmentDAO departmentDAO = new DepartmentDAO();
                    departmentDAO.updateDepartmentStatus(selected.getDepartmentId(), 0);
                    statusLabel.setText("Departamento eliminado correctamente.");
                    statusLabel.setTextFill(Color.GREEN);
                    loadDepartments();
                } else {
                    statusLabel.setText("Eliminación cancelada.");
                    statusLabel.setTextFill(Color.BLACK);
                }
            } catch (SQLException e) {
                String sqlState = e.getSQLState();
                if (sqlState != null && sqlState.equals("08001")) {
                    statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                } else if (sqlState != null && sqlState.equals("08S01")) {
                    statusLabel.setText("Error de interrupcion de conexión con la base de datos. Por favor, intente más tarde.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Error de interrupcion de conexión con la base de datos: {}", e.getMessage(), e);
                } else if (sqlState != null && sqlState.equals("42S02")) {
                    statusLabel.setText("Tabla de departamentos no encontrada. Por favor, verifique la configuración.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Tabla de departamentos no encontrada: {}", e.getMessage(), e);
                } else if (sqlState != null && sqlState.equals("42S22")) {
                    statusLabel.setText("Columna de departamentos no encontrada. Por favor, verifique la configuración.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Columna de departamentos no encontrada: {}", e.getMessage(), e);
                } else if (sqlState != null && sqlState.equals("HY000")) {
                    statusLabel.setText("Error general de la base de datos. Por favor, intente más tarde.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
                } else if (sqlState != null && sqlState.equals("42000")) {
                    statusLabel.setText("Base de datos desconocida. Por favor, verifique la configuración.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                } else if (sqlState != null && sqlState.equals("28000")) {
                    statusLabel.setText("Acceso denegado a la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                } else {
                    statusLabel.setText("Error al cargar departamentos.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Error al cargar departamentos: {}", e.getMessage(), e);
                }
            } catch (IOException e) {
                statusLabel.setText("No se pudo leer el archivo de configuracion de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de E/S al eliminar departamento: {}", e.getMessage(), e);
            } catch (Exception e) {
                statusLabel.setText("Ocurrió un error inesperado al eliminar el departamento.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error inesperado al eliminar departamento: {}", e.getMessage(), e);
            }
        }
    }
}