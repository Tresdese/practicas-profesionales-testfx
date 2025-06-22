package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DTO.PeriodDTO;
import logic.DAO.PeriodDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.sql.SQLException;

public class GUI_RegisterPeriodController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_RegisterPeriodController.class);

    @FXML
    private Label statusLabel;

    @FXML
    private TextField periodLabel, nameField;

    @FXML
    private DatePicker startDateLabel, endDateField;

    @FXML
    private Button registerButton;

    @FXML
    public void initialize() {
        registerButton.setOnAction(event -> handleRegisterPeriod());
    }

    @FXML
    private void handleRegisterPeriod() {
        try {
            if (!areFieldsFilled()) {
                statusLabel.setText("Todos los campos deben estar llenos.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                return;
            }

            LocalDate startLocalDate = startDateLabel.getValue();
            LocalDate endLocalDate = endDateField.getValue();

            Timestamp startDate = Timestamp.valueOf(startLocalDate.atStartOfDay());
            Timestamp endDate = Timestamp.valueOf(endLocalDate.atStartOfDay());

            PeriodDTO period = new PeriodDTO(
                    periodLabel.getText(),
                    nameField.getText(),
                    startDate,
                    endDate
            );

            PeriodDAO periodDAO = new PeriodDAO();
            boolean success = periodDAO.insertPeriod(period);

            if (success) {
                statusLabel.setText("¡Periodo registrado exitosamente!");
                statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            } else {
                statusLabel.setText("No se pudo registrar el periodo.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al registrar el periodo: ");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                LOGGER.error("Error al registrar el periodo: {}", e.getMessage(), e);
            }
        } catch (NullPointerException e) {
            LOGGER.error("Referencia nula al registrar el periodo: {}", e.getMessage(), e);
            statusLabel.setText("Error interno al registrar el periodo.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        } catch (Exception e) {
            LOGGER.error("Error al registrar el periodo: {}", e.getMessage(), e);
            statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    private boolean areFieldsFilled() {
        return !periodLabel.getText().isEmpty() &&
                !nameField.getText().isEmpty() &&
                startDateLabel.getValue() != null &&
                endDateField.getValue() != null;
    }
}