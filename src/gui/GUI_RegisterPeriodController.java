package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import logic.DTO.PeriodDTO;
import logic.services.PeriodService;
import logic.exceptions.RepeatedId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
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
                statusLabel.setTextFill(Color.RED);
                return;
            }

            // Validación: el ID solo puede contener números
            String periodId = periodLabel.getText().trim();
            if (!periodId.matches("\\d+")) {
                statusLabel.setText("El ID del periodo solo puede contener números.");
                statusLabel.setTextFill(Color.RED);
                return;
            }

            LocalDate startLocalDate = startDateLabel.getValue();
            LocalDate endLocalDate = endDateField.getValue();

            Timestamp startDate = Timestamp.valueOf(startLocalDate.atStartOfDay());
            Timestamp endDate = Timestamp.valueOf(endLocalDate.atStartOfDay());

            PeriodDTO period = new PeriodDTO(
                    periodId,
                    nameField.getText(),
                    startDate,
                    endDate
            );

            PeriodService periodService = new PeriodService();
            periodService.registerPeriod(period);

            statusLabel.setText("¡Periodo registrado exitosamente!");
            statusLabel.setTextFill(Color.GREEN);

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
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else if ("HY000".equals(sqlState)) {
                statusLabel.setText("Error general de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
            } else if ("42S02".equals(sqlState)) {
                statusLabel.setText("Tabla no encontrada en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if ("42S22".equals(sqlState)) {
                statusLabel.setText("Columna no encontrada en la tabla.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada en la tabla: {}", e.getMessage(), e);
            } else if ("22001".equals(sqlState)) {
                statusLabel.setText("Datos demasiado largos para el campo.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Datos demasiado largos para el campo: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error de base de datos al registrar el periodo: ");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de base de datos al registrar el periodo: {}", e.getMessage(), e);
            }
        } catch (RepeatedId e) {
            statusLabel.setText("El ID del periodo ya existe.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al registrar el periodo: {}", e.getMessage(), e);
        } catch (NullPointerException e) {
            LOGGER.error("Referencia nula al registrar el periodo: {}", e.getMessage(), e);
            statusLabel.setText("Error interno al registrar el periodo.");
            statusLabel.setTextFill(Color.RED);
        } catch (IOException e) {
            LOGGER.error("Error al leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
            statusLabel.setText("Error al leer la configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
        } catch (IllegalArgumentException e) {
            LOGGER.error(e);
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al registrar el periodo: {}", e.getMessage(), e);
            statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    private boolean areFieldsFilled() {
        return !periodLabel.getText().trim().isEmpty() &&
                !nameField.getText().trim().isEmpty() &&
                startDateLabel.getValue() != null &&
                endDateField.getValue() != null;
    }
}