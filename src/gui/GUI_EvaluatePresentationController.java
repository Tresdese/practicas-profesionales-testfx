package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import logic.DAO.AssessmentCriterionDAO;
import logic.DAO.EvaluationDetailDAO;
import logic.DAO.EvaluationPresentationDAO;
import logic.DTO.AssessmentCriterionDTO;
import logic.DTO.EvaluationDetailDTO;
import logic.DTO.EvaluationPresentationDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GUI_EvaluatePresentationController {

    private static final Logger logger = LogManager.getLogger(GUI_EvaluatePresentationController.class);

    @FXML
    private VBox criteriaContainer;

    @FXML
    private Button saveButton;

    private final AssessmentCriterionDAO assessmentCriterionDAO = new AssessmentCriterionDAO();
    private final EvaluationDetailDAO evaluationDetailDAO = new EvaluationDetailDAO();
    private final EvaluationPresentationDAO evaluationPresentationDAO = new EvaluationPresentationDAO();

    private List<TextField> scoreFields = new ArrayList<>();
    private List<AssessmentCriterionDTO> criteriaList;
    private int presentationId;
    private String tuiton;

    @FXML
    public void initialize() {
        saveButton.setOnAction(event -> saveScores());
    }

    public void loadCriteria() {
        try {
            criteriaList = assessmentCriterionDAO.getAllAssessmentCriteria();
            for (AssessmentCriterionDTO criterion : criteriaList) {
                HBox hBox = new HBox(10);
                Label label = new Label(criterion.getNameCriterion());
                TextField textField = new TextField();
                scoreFields.add(textField);
                hBox.getChildren().addAll(label, textField);
                criteriaContainer.getChildren().add(hBox);
            }
        } catch (SQLException e) {
            logger.error("Error al cargar los criterios de evaluación.", e);
        }
    }

    public void setPresentationIdAndTuiton(int presentationId, String tuiton) {
        this.presentationId = presentationId;
        this.tuiton = tuiton;
        logger.info("ID de la presentación configurado: " + presentationId + ", Matrícula: " + tuiton);
    }

    private void saveScores() {
        if (criteriaList == null || criteriaList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No hay criterios de evaluación cargados.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        List<EvaluationDetailDTO> evaluationDetails = new ArrayList<>();
        double totalScore = 0.0;

        try {
            for (int i = 0; i < criteriaList.size(); i++) {
                String scoreText = scoreFields.get(i).getText();
                if (scoreText == null || scoreText.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Hay calificaciones vacías. Por favor, completa todas las calificaciones.", ButtonType.OK);
                    alert.showAndWait();
                    return;
                }

                double score;
                try {
                    score = Double.parseDouble(scoreText);
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Ingrese valores numéricos válidos.", ButtonType.OK);
                    alert.showAndWait();
                    return;
                }

                EvaluationDetailDTO detail = new EvaluationDetailDTO(
                        0,
                        0,
                        Integer.parseInt(criteriaList.get(i).getIdCriterion()),
                        score
                );
                evaluationDetails.add(detail);
                totalScore += score;
            }

            double averageScore = totalScore / evaluationDetails.size();

            EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                    0,
                    presentationId,
                    tuiton,
                    new java.util.Date(),
                    averageScore
            );
            int evaluationId = evaluationPresentationDAO.insertEvaluationPresentation(evaluation);

            for (EvaluationDetailDTO detail : evaluationDetails) {
                detail.setIdEvaluation(evaluationId); // Asignar el ID de evaluación generado
                evaluationDetailDAO.insertEvaluationDetail(detail); // Insertar detalle
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Calificaciones guardadas exitosamente.", ButtonType.OK);
            alert.showAndWait();
        } catch (Exception e) {
            logger.error("Error al guardar las calificaciones.", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ocurrió un error al guardar las calificaciones.", ButtonType.OK);
            alert.showAndWait();
        }
    }
}