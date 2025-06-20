package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DTO.PeriodDTO;
import logic.DAO.PeriodDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.time.LocalDate;

public class GUI_RegisterPeriodController {

    private static final Logger logger = LogManager.getLogger(GUI_RegisterPeriodController.class);

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
        } catch (java.sql.SQLException e) {
            logger.error("Error de base de datos al registrar el periodo: {}", e.getMessage(), e);
            statusLabel.setText("Error de base de datos al registrar el periodo.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        } catch (NullPointerException e) {
            logger.error("Referencia nula al registrar el periodo: {}", e.getMessage(), e);
            statusLabel.setText("Error interno al registrar el periodo.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        } catch (Exception e) {
            logger.error("Error al registrar el periodo: {}", e.getMessage(), e);
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