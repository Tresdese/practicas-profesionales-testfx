package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import logic.DAO.CriterionSelfAssessmentDAO;
import logic.DAO.EvidenceDAO;
import logic.DAO.SelfAssessmentDAO;
import logic.DTO.CriterionSelfAssessmentDTO;
import logic.DTO.SelfAssessmentDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Desktop;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;

public class GUI_CheckSelfAssessmentController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_CheckSelfAssessmentController.class);

    @FXML
    private Label labelComments;
    @FXML
    private Label labelGrade;
    @FXML
    private Label labelStatus;
    @FXML
    private ListView<String> listViewCriteria;
    @FXML
    private Button buttonViewEvidence;

    private int evidenceId = -1;

    public void setStudentTuition(String tuition) {
        try {
            SelfAssessmentDTO selfAssessment = getSelfAssessmentByTuition(tuition);
            if (selfAssessment != null) {
                evidenceId = selfAssessment.getEvidenceId();
                updateSelfAssessmentLabels(selfAssessment);
                List<CriterionSelfAssessmentDTO> criterios = getCriteriaBySelfAssessmentId(selfAssessment.getSelfAssessmentId());
                updateCriteriaList(criterios);
            } else {
                clearInterface("No registrada", "-", "-");
            }
        } catch (SQLException e) {
            LOGGER.error("Error de base de datos al obtener la autoevaluación: {}", e.getMessage(), e);
            clearInterface("Error BD", "Error BD", "Error BD");
        } catch (NullPointerException e) {
            LOGGER.error("Referencia nula al obtener la autoevaluación: {}", e.getMessage(), e);
            clearInterface("Error", "Error", "Error");
        } catch (Exception e) {
            LOGGER.error("Error inesperado al obtener la autoevaluación: {}", e.getMessage(), e);
            clearInterface("Error", "Error", "Error");
        }
    }

    private SelfAssessmentDTO getSelfAssessmentByTuition(String tuition) throws SQLException {
        SelfAssessmentDAO dao = new SelfAssessmentDAO();
        return dao.getAllSelfAssessments()
                .stream()
                .filter(sa -> sa.getRegistration().equalsIgnoreCase(tuition))
                .findFirst()
                .orElse(null);
    }

    private List<CriterionSelfAssessmentDTO> getCriteriaBySelfAssessmentId(int selfAssessmentId) throws SQLException {
        CriterionSelfAssessmentDAO criterionDAO = new CriterionSelfAssessmentDAO();
        return criterionDAO.getAllCriterionSelfAssessments()
                .stream()
                .filter(c -> c.getIdSelfAssessment() == selfAssessmentId)
                .toList();
    }

    private void updateSelfAssessmentLabels(SelfAssessmentDTO selfAssessment) {
        labelComments.setText(selfAssessment.getComments());
        labelGrade.setText(String.valueOf(selfAssessment.getGrade()));
        labelStatus.setText(selfAssessment.getStatus().getValue());
    }

    private void updateCriteriaList(List<CriterionSelfAssessmentDTO> criterios) {
        listViewCriteria.getItems().clear();
        for (var criterio : criterios) {
            listViewCriteria.getItems().add(
                    "Criterio ID: " + criterio.getIdCriteria() +
                            " | Calificación: " + criterio.getGrade() +
                            " | Comentarios: " + criterio.getComments()
            );
        }
    }

    private void clearInterface(String comments, String grade, String status) {
        labelComments.setText(comments);
        labelGrade.setText(grade);
        labelStatus.setText(status);
        evidenceId = -1;
        listViewCriteria.getItems().clear();
    }

    @FXML
    private void handleViewEvidence() {
        if (evidenceId <= 0) {
            LOGGER.warn("No hay evidencia vinculada.");
            return;
        }
        try {
            EvidenceDAO evidenceDAO = new EvidenceDAO();
            String url = evidenceDAO.searchEvidenceById(evidenceId).getRoute();
            if (url != null && !url.isEmpty()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI(url));
                } else {
                    LOGGER.error("Desktop no soportado para abrir URLs.");
                }
            } else {
                LOGGER.warn("No se encontró URL de evidencia para el id: {}", evidenceId);
            }
        } catch (SQLException e) {
            LOGGER.error("Error de base de datos al buscar evidencia: {}", e.getMessage(), e);
        } catch (NullPointerException e) {
            LOGGER.error("No se encontró evidencia para el id proporcionado: {} - {}", evidenceId, e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("No se pudo abrir la evidencia: {}", e.getMessage(), e);
        }
    }
}