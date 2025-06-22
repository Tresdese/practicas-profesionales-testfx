package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

    private static final Logger LOGGER = LogManager.getLogger(GUI_DetailsPresentationStudentController.class);

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
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
            } else if (sqlState != null && sqlState.equals("42000")) {
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
            } else if (sqlState != null && sqlState.equals("28000")) {
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
            } else {
                LOGGER.error("Error al cargar los detalles de la evaluación: {}", e.getMessage(), e);
                statusLabel.setText("Error al cargar los detalles.");
                statusLabel.setTextFill(Color.RED);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar los detalles de la evaluación: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al cargar los detalles.");
            statusLabel.setTextFill(Color.RED);
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