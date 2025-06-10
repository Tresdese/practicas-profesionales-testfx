package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DTO.ScheduleOfActivitiesDTO;
import logic.DTO.EvidenceDTO;
import logic.DAO.ScheduleOfActivitiesDAO;
import logic.DAO.EvidenceDAO;
import logic.exceptions.EmptyFields;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public class GUI_RegisterActivityScheduleController {

    private static final Logger logger = LogManager.getLogger(GUI_RegisterActivityScheduleController.class);

    @FXML
    private Label statusLabel;

    @FXML
    private TextField fieldMilestone, fieldTuition;

    @FXML
    private DatePicker fieldEstimatedDate;

    @FXML
    private ChoiceBox<EvidenceDTO> choiceEvidence;

    private ScheduleOfActivitiesDAO scheduleDAO;
    private EvidenceDAO evidenceDAO;

    @FXML
    public void initialize() {
        scheduleDAO = new ScheduleOfActivitiesDAO();
        evidenceDAO = new EvidenceDAO();
        loadEvidences();
    }

    private void loadEvidences() {
        try {
            List<EvidenceDTO> evidences = evidenceDAO.getAllEvidences();
            choiceEvidence.setItems(FXCollections.observableArrayList(evidences));
            choiceEvidence.setConverter(new javafx.util.StringConverter<EvidenceDTO>() {
                @Override
                public String toString(EvidenceDTO evidence) {
                    return evidence != null ? evidence.getEvidenceName() : "";
                }
                @Override
                public EvidenceDTO fromString(String string) {
                    return null;
                }
            });
        } catch (SQLException e) {
            logger.error("Error al cargar evidencias: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleRegisterActivitySchedule() {
        try {
            if (!areFieldsFilled()) {
                throw new EmptyFields("Todos los campos deben estar llenos.");
            }

            String milestone = fieldMilestone.getText();
            LocalDate localDate = fieldEstimatedDate.getValue();
            Timestamp estimatedDate = localDate != null ? Timestamp.valueOf(localDate.atStartOfDay()) : null;
            String tuition = fieldTuition.getText();
            EvidenceDTO selectedEvidence = choiceEvidence.getValue();
            String idEvidence = selectedEvidence != null ? String.valueOf(selectedEvidence.getIdEvidence()) : null;

            ScheduleOfActivitiesDTO schedule = new ScheduleOfActivitiesDTO(
                    null, milestone, estimatedDate, tuition, idEvidence
            );

            boolean inserted = scheduleDAO.insertScheduleOfActivities(schedule);
            if (inserted) {
                statusLabel.setText("¡Cronograma registrado exitosamente!");
                statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            } else {
                statusLabel.setText("No se pudo registrar el cronograma.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            }

        } catch (SQLException e) {
            logger.error("Error de SQL al registrar el cronograma: {}", e.getMessage(), e);
            statusLabel.setText("Error de conexión con la base de datos. Intente más tarde.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        } catch (EmptyFields e) {
            logger.warn("Error de validación: {}", e.getMessage(), e);
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    public boolean areFieldsFilled() {
        return  !fieldMilestone.getText().isEmpty() &&
                fieldEstimatedDate.getValue() != null &&
                !fieldTuition.getText().isEmpty() &&
                choiceEvidence.getValue() != null;
    }
}