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

    private static final Logger LOGGER = LogManager.getLogger(GUI_EvaluatePresentationController.class);

    @FXML
    private VBox criteriaInputContainer;

    @FXML
    private Button saveButton;

    @FXML
    private TextField averageGradeField;

    @FXML
    private TextArea commentArea;

    @FXML
    private Label commentCharCountLabel;

    private static final int MAX_COMMENT_LENGTH = 500;

    private final AssessmentCriterionDAO assessmentCriterionDAO = new AssessmentCriterionDAO();
    private final EvaluationDetailDAO evaluationDetailDAO = new EvaluationDetailDAO();
    private final EvaluationPresentationDAO evaluationPresentationDAO = new EvaluationPresentationDAO();

    private final List<TextField> scoreFields = new ArrayList<>();
    private List<AssessmentCriterionDTO> criteriaList;
    private int presentationId;
    private String tuition;

    @FXML
    public void initialize() {

        saveButton.setOnAction(event -> saveScores());
        configureCharCountLabels();
        configureTextFormatters();
        loadCriteria();
    }

    public void loadCriteria() {
        try {

            criteriaInputContainer.getChildren().clear();
            scoreFields.clear();

            criteriaList = assessmentCriterionDAO.getAllAssessmentCriteria();

            if (criteriaList == null || criteriaList.isEmpty()) {
                Label emptyLabel = new Label("No hay criterios de evaluación disponibles.");
                criteriaInputContainer.getChildren().add(emptyLabel);
                return;
            }

            for (AssessmentCriterionDTO criterion : criteriaList) {
                HBox hBox = new HBox(10);

                Label nameLabel = new Label(criterion.getNameCriterion());
                nameLabel.setPrefWidth(300);

                TextField scoreField = new TextField();
                scoreField.setPrefWidth(50);
                configureScoreField(scoreField);

                Label competentLabel = new Label("Competente");
                Label independentLabel = new Label("Independiente");
                Label basicLabel = new Label("Básico");
                Label notCompetentLabel = new Label("No Competente");

                competentLabel.getStyleClass().add("level-label");
                independentLabel.getStyleClass().add("level-label");
                basicLabel.getStyleClass().add("level-label");
                notCompetentLabel.getStyleClass().add("level-label");

                scoreField.textProperty().addListener((observable, oldValue, newValue) -> {
                    highlightLevel(competentLabel, independentLabel, basicLabel, notCompetentLabel, newValue);
                });

                hBox.getChildren().addAll(nameLabel, scoreField, competentLabel, independentLabel, basicLabel, notCompetentLabel);
                criteriaInputContainer.getChildren().add(hBox);

                scoreFields.add(scoreField);
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                showAlert("Error de conexión con la base de datos. Por favor, verifica tu conexión.");
            } else if (sqlState != null && sqlState.equals("08S01")) {
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                showAlert("Conexión interrumpida con la base de datos. Intenta más tarde.");
            } else if (sqlState != null && sqlState.equals("42000")) {
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                showAlert("La base de datos no está disponible.");
            } else if (sqlState != null && sqlState.equals("28000")) {
                LOGGER.error("Acceso denegado a la base de datos: {} " , e.getMessage(), e);
                showAlert("Acceso denegado a la base de datos.");
            } else {
                LOGGER.error("Error al cargar los criterios de evaluación: {}", e.getMessage(), e);
                showAlert("Ocurrió un error al cargar los criterios. Intenta más tarde.");
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar los criterios de evaluación: {} ", e.getMessage(), e);
            showAlert("Ocurrió un error inesperado. Intenta más tarde.");
        }
    }

    private void configureScoreField(TextField scoreField) {
        scoreField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{1,2}(\\.\\d{0,1})?")) {
                try {
                    double value = Double.parseDouble(newText);
                    if (value >= 1 && value <= 10) {
                        return change;
                    }
                } catch (NumberFormatException e) {
                    LOGGER.error("Formato de calificación inválido", e);
                }
            }
            change.setText("");
            return change;
        }));

        scoreField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty() && newValue.matches("\\d{1,2}(\\.\\d{0,1})?")) {
                try {
                    double score = Double.parseDouble(newValue);
                } catch (NumberFormatException e) {
                    LOGGER.error("Formato incorrecto de los valores: " + newValue, e);
                }
            } else {
                LOGGER.warn("El valor ingresado no cumple con el formato esperado: " + newValue);
            }
        });
    }

    private void configureCharCount(TextArea textArea, Label charCountLabel, int maxLength) {
        charCountLabel.setText("0/" + maxLength);
        textArea.textProperty().addListener((obs, oldText, newText) ->
                charCountLabel.setText(newText.length() + "/" + maxLength)
        );
    }

    private void configureCharCountLabels() {
        configureCharCount(commentArea, commentCharCountLabel, MAX_COMMENT_LENGTH);
    }

    private void configureTextAreaFormatter(TextArea textArea, int maxLength) {
        textArea.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= maxLength ? change : null
        ));
    }

    private void configureTextFormatters() {
        configureTextAreaFormatter(commentArea, MAX_COMMENT_LENGTH);
    }

    private void highlightLevel(Label competentLabel, Label independentLabel, Label basicLabel, Label notCompetentLabel, String scoreText) {
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
            } else if (score >= 0.0 && score <= 7.0) {
                notCompetentLabel.getStyleClass().add("highlighted");
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Formato incorrecto de los valores. {}", e.getMessage(), e);
            showAlert("Por favor, ingrese valores numéricos válidos.");
        } catch (Exception e) {
            LOGGER.error("Error inesperado al resaltar el nivel de competencia: {}", e.getMessage(), e);
            showAlert("Ocurrió inesperado un error al resaltar el nivel de competencia.");
        }
    }

    public void setPresentationIdAndTuiton(int presentationId, String tuiton) {
        this.presentationId = presentationId;
        this.tuition = tuiton;
        LOGGER.info("ID de la presentación configurado: " + presentationId + ", Matrícula: " + tuiton);
    }

    private void saveScores() {
        if (criteriaList == null || criteriaList.isEmpty()) {
            showAlert("No hay criterios de evaluación disponibles. Por favor, verifica la configuración.");
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
            averageGradeField.setText(String.format("%.2f", averageScore));

            EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                    0,
                    presentationId,
                    tuition,
                    new java.util.Date(),
                    commentArea.getText(),
                    averageScore
            );
            int evaluationId = evaluationPresentationDAO.insertEvaluationPresentation(evaluation);

            for (EvaluationDetailDTO detail : evaluationDetails) {
                detail.setIdEvaluation(evaluationId);
                evaluationDetailDAO.insertEvaluationDetail(detail);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Calificaciones guardadas exitosamente.", ButtonType.OK);
            alert.showAndWait();
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                LOGGER.error("Error de conexión con la base de datos: " + e.getMessage(), e);
                showAlert("Error de conexión con la base de datos. Por favor, verifica tu conexión.");
            } else if (sqlState != null && sqlState.equals("08S01")) {
                LOGGER.error("Conexión interrumpida con la base de datos: " + e.getMessage(), e);
                showAlert("Conexión interrumpida con la base de datos. Intenta más tarde.");
            } else if (sqlState != null && sqlState.equals("42000")) {
                LOGGER.error("Base de datos desconocida: " + e.getMessage(), e);
                showAlert("La base de datos no está disponible.");
            } else if (sqlState != null && sqlState.equals("28000")) {
                LOGGER.error("Acceso denegado a la base de datos: " + e.getMessage(), e);
                showAlert("Acceso denegado a la base de datos.");
            } else if (sqlState != null && sqlState.equals("23000")) {
                LOGGER.error("Violación de restricción de integridad: " + e.getMessage(), e);
                showAlert("Violación de restricción de integridad. Verifica los datos ingresados.");
            } else {
                LOGGER.error("Error al guardar las calificaciones: " + e.getMessage(), e);
                showAlert("Ocurrió un error al guardar las calificaciones.");
            }
        } catch (Exception e) {
            LOGGER.error("Error al guardar las calificaciones.", e);
            showAlert("Ocurrió un error inesperado al guardar las calificaciones. Intenta más tarde.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}