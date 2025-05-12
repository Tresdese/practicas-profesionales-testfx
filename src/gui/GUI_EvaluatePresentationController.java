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
    private VBox criteriaInputContainer;

    @FXML
    private Button saveButton;

    @FXML
    private TextField averageField;

    private final AssessmentCriterionDAO assessmentCriterionDAO = new AssessmentCriterionDAO();
    private final EvaluationDetailDAO evaluationDetailDAO = new EvaluationDetailDAO();
    private final EvaluationPresentationDAO evaluationPresentationDAO = new EvaluationPresentationDAO();

    private final List<TextField> scoreFields = new ArrayList<>();
    private List<AssessmentCriterionDTO> criteriaList;
    private int presentationId;
    private String tuiton;

    @FXML
    public void initialize() {
        // Configurar acción del botón para guardar
        saveButton.setOnAction(event -> saveScores());

        // Cargar los criterios
        loadCriteria();
    }

    public void loadCriteria() {
        try {
            // Limpiar contenedores y listas para evitar duplicación
            criteriaInputContainer.getChildren().clear();
            scoreFields.clear();

            // Obtener los criterios de evaluación desde la base de datos
            criteriaList = assessmentCriterionDAO.getAllAssessmentCriteria();

            for (AssessmentCriterionDTO criterion : criteriaList) {
                HBox hBox = new HBox(10);

                // Etiqueta del criterio
                Label nameLabel = new Label(criterion.getNameCriterion());
                nameLabel.setPrefWidth(300);

                // Campo para ingresar la calificación
                TextField scoreField = new TextField();
                scoreField.setPrefWidth(50);

                // Etiquetas para los niveles de calificación
                Label competentLabel = new Label("Competente");
                Label independentLabel = new Label("Independiente");
                Label basicLabel = new Label("Básico");
                Label notCompetentLabel = new Label("No Competente");

                // Estilo inicial de las etiquetas
                competentLabel.getStyleClass().add("level-label");
                independentLabel.getStyleClass().add("level-label");
                basicLabel.getStyleClass().add("level-label");
                notCompetentLabel.getStyleClass().add("level-label");

                // Agregar listener para subrayar dinámicamente el nivel correspondiente
                scoreField.textProperty().addListener((observable, oldValue, newValue) -> {
                    highlightLevel(competentLabel, independentLabel, basicLabel, notCompetentLabel, newValue);
                });

                // Agregar los elementos al contenedor
                hBox.getChildren().addAll(nameLabel, scoreField, competentLabel, independentLabel, basicLabel, notCompetentLabel);
                criteriaInputContainer.getChildren().add(hBox);

                // Guardar referencia al campo de calificación
                scoreFields.add(scoreField);
            }
        } catch (SQLException e) {
            logger.error("Error al cargar los criterios de evaluación.", e);
        }
    }

    private void highlightLevel(Label competentLabel, Label independentLabel, Label basicLabel, Label notCompetentLabel, String scoreText) {
        // Limpiar estilos previos
        competentLabel.getStyleClass().remove("highlighted");
        independentLabel.getStyleClass().remove("highlighted");
        basicLabel.getStyleClass().remove("highlighted");
        notCompetentLabel.getStyleClass().remove("highlighted");

        try {
            double score = Double.parseDouble(scoreText);
            if (score >= 9.1) {
                competentLabel.getStyleClass().add("highlighted");
            } else if (score >= 8.1) {
                independentLabel.getStyleClass().add("highlighted");
            } else if (score >= 7.1) {
                basicLabel.getStyleClass().add("highlighted");
            } else if (score >= 5.0) {
                notCompetentLabel.getStyleClass().add("highlighted");
            }
        } catch (NumberFormatException e) {
            // Si el texto no es un número válido, no se realiza ninguna acción
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
                        presentationId,
                        Integer.parseInt(criteriaList.get(i).getIdCriterion()),
                        score
                );
                evaluationDetails.add(detail);
                totalScore += score;
            }

            double averageScore = totalScore / evaluationDetails.size();
            averageField.setText(String.format("%.2f", averageScore));

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