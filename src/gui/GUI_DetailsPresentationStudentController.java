package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import logic.DAO.AssessmentCriterionDAO;
import logic.DAO.EvaluationDetailDAO;
import logic.DTO.AssessmentCriterionDTO;
import logic.DTO.EvaluationDetailDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class GUI_DetailsPresentationStudentController {

    @FXML
    private VBox detailsVBox;
    @FXML
    private Label statusLabel;

    private static final Logger logger = LogManager.getLogger(GUI_DetailsPresentationStudentController.class);

    public void setIdEvaluation(int idEvaluation) {
        detailsVBox.getChildren().clear();
        try {
            List<EvaluationDetailDTO> details = getDetailsByEvaluation(idEvaluation);
            if (details == null || details.isEmpty()) {
                statusLabel.setText("No hay detalles para esta evaluación.");
            } else {
                for (EvaluationDetailDTO detail : details) {
                    String criterionName = getCriterionName(detail.getIdCriteria());
                    Label label = new Label("Criterio: " + criterionName + "   |   Calificación: " + detail.getGrade());
                    label.setStyle("-fx-font-size: 15px; -fx-text-fill: #333;");
                    detailsVBox.getChildren().add(label);
                }
                statusLabel.setText("");
            }
        } catch (SQLException e) {
            logger.error("Error de base de datos al cargar los detalles de la evaluación: {}", e.getMessage(), e);
            statusLabel.setText("Error de base de datos al cargar los detalles.");
        } catch (Exception e) {
            logger.error("Error inesperado al cargar los detalles de la evaluación: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al cargar los detalles.");
        }
    }

    private List<EvaluationDetailDTO> getDetailsByEvaluation(int idEvaluation) throws SQLException {
        EvaluationDetailDAO detailDAO = new EvaluationDetailDAO();
        return detailDAO.getAllEvaluationDetails()
                .stream()
                .filter(d -> d.getIdEvaluation() == idEvaluation)
                .collect(Collectors.toList());
    }

    private String getCriterionName(int idCriterion) throws SQLException {
        AssessmentCriterionDAO criterionDAO = new AssessmentCriterionDAO();
        AssessmentCriterionDTO criterion = criterionDAO.searchAssessmentCriterionById(String.valueOf(idCriterion));
        return criterion != null ? criterion.getNameCriterion() : "Desconocido";
    }
}