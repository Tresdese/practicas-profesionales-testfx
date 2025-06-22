package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import logic.DAO.CriterionSelfAssessmentDAO;
import logic.DAO.EvidenceDAO;
import logic.DAO.SelfAssessmentCriteriaDAO;
import logic.DAO.SelfAssessmentDAO;
import logic.DTO.CriterionSelfAssessmentDTO;
import logic.DTO.SelfAssessmentDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Collections;

public class GUI_CheckSelfAssessmentController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_CheckSelfAssessmentController.class);

    @FXML
    private Label commentsLabel;
    @FXML
    private Label gradeLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label noSelfAssessmentLabel;
    @FXML
    private ListView<String> criteriaListView;
    @FXML
    private Button viewEvidenceButton;

    private int evidenceId = -1;

    public void setStudentTuition(String tuition) {
        try {
            SelfAssessmentDTO selfAssessment = getSelfAssessmentByTuition(tuition);
            if (selfAssessment != null) {
                evidenceId = selfAssessment.getEvidenceId();
                updateSelfAssessmentLabels(selfAssessment);
                List<CriterionSelfAssessmentDTO> criterios = getCriteriaBySelfAssessmentId(selfAssessment.getSelfAssessmentId());
                updateCriteriaList(criterios);
                noSelfAssessmentLabel.setVisible(false);
                viewEvidenceButton.setDisable(false);
            } else {
                clearInterface("No registrada", "-", "-");
                noSelfAssessmentLabel.setVisible(true);
                viewEvidenceButton.setDisable(true);
            }
        } catch (NullPointerException e) {
            LOGGER.error("Referencia nula al obtener la autoevaluación: {}", e.getMessage(), e);
            clearInterface("Error de referendia nula", "Error de referencia nula", "Error de referencia nula");
        } catch (Exception e) {
            LOGGER.error("Error inesperado al obtener la autoevaluación: {}", e.getMessage(), e);
            clearInterface("Error inesperado", "Error inesperado", "Error inesperado");
        }
    }

    private SelfAssessmentDTO getSelfAssessmentByTuition(String tuition) {
        try {
            SelfAssessmentDAO dao = new SelfAssessmentDAO();
            return dao.getAllSelfAssessments()
                    .stream()
                    .filter(sa -> sa.getRegistration().equalsIgnoreCase(tuition))
                    .findFirst()
                    .orElse(null);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return null;
            } else if (sqlState != null && sqlState.equals("42000")) {
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                return null;
            } else if (sqlState != null && sqlState.equals("28000")) {
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return null;
            } else if (sqlState != null && sqlState.equals("08S01")) {
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return null;
            } else {
                LOGGER.error("Error de base de datos al buscar la autoevaluación por matrícula: {}", e.getMessage(), e);
                statusLabel.setText("Error de base de datos al buscar la autoevaluación.");
                statusLabel.setTextFill(Color.RED);
                return null;
            }
        } catch (IOException e) {
            LOGGER.error("Error al leer la configuración de la base de datos: {}", e.getMessage(), e);
            statusLabel.setText("Error al leer la configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            return null;
        } catch (Exception e) {
            LOGGER.error("Error inesperado al buscar la autoevaluación por matrícula: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al buscar la autoevaluación.");
            statusLabel.setTextFill(Color.RED);
            return null;
        }
    }

    private List<CriterionSelfAssessmentDTO> getCriteriaBySelfAssessmentId(int selfAssessmentId) {
        try {
            CriterionSelfAssessmentDAO criterionDAO = new CriterionSelfAssessmentDAO();
            return criterionDAO.getAllCriterionSelfAssessments()
                    .stream()
                    .filter(c -> c.getIdSelfAssessment() == selfAssessmentId)
                    .toList();
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return Collections.emptyList();
            } else if (sqlState != null && sqlState.equals("42000")) {
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                return Collections.emptyList();
            } else if (sqlState != null && sqlState.equals("28000")) {
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return Collections.emptyList();
            } else if (sqlState != null && sqlState.equals("08S01")) {
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return Collections.emptyList();
            } else {
                LOGGER.error("Error de base de datos al obtener los criterios de autoevaluación: {}", e.getMessage(), e);
                statusLabel.setText("Error de base de datos al obtener los criterios.");
                statusLabel.setTextFill(Color.RED);
                return Collections.emptyList();
            }
        } catch (IOException e) {
            LOGGER.error("Error al leer la configuración de la base de datos: {}", e.getMessage(), e);
            statusLabel.setText("Error al leer la configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            return java.util.Collections.emptyList();
        } catch (Exception e) {
            LOGGER.error("Error inesperado al obtener los criterios de autoevaluación: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al obtener los criterios.");
            statusLabel.setTextFill(Color.RED);
            return java.util.Collections.emptyList();
        }
    }

    private void updateSelfAssessmentLabels(SelfAssessmentDTO selfAssessment) {
        commentsLabel.setText(selfAssessment.getComments());
        gradeLabel.setText(String.valueOf(selfAssessment.getGrade()));
        statusLabel.setText(selfAssessment.getStatus().getValue());
    }

    private void updateCriteriaList(List<CriterionSelfAssessmentDTO> criterios) {
        criteriaListView.getItems().clear();
        SelfAssessmentCriteriaDAO selfAssessmentCriteriaDAO = new SelfAssessmentCriteriaDAO();
        for (var criterio : criterios) {
            String nombreCriterio = "Desconocido";
            try {
                String idCriterio = String.valueOf(criterio.getIdCriteria());
                var dto = selfAssessmentCriteriaDAO.searchSelfAssessmentCriteriaById(idCriterio);
                nombreCriterio = dto.getNameCriteria();
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
                    LOGGER.error("Error de base de datos al obtener el nombre del criterio: {}", e.getMessage(), e);
                    statusLabel.setText("Error de base de datos al obtener el criterio.");
                    statusLabel.setTextFill(Color.RED);
                }
            } catch (IOException e) {
                LOGGER.error("Error al leer la configuración de la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Error al leer la configuración de la base de datos.");
                statusLabel.setTextFill(Color.RED);
            } catch (Exception e) {
                LOGGER.error("Error inesperado al obtener el nombre del criterio: {}", e.getMessage(), e);
                statusLabel.setText("Error inesperado al obtener el criterio.");
                statusLabel.setTextFill(Color.RED);
            }
            criteriaListView.getItems().add(
                    "Criterio: " + nombreCriterio +
                            " | Calificación: " + criterio.getGrade() +
                            " | Comentarios: " + criterio.getComments()
            );
        }
    }

    private void clearInterface(String comments, String grade, String status) {
        commentsLabel.setText(comments);
        gradeLabel.setText(grade);
        statusLabel.setText(status);
        evidenceId = -1;
        criteriaListView.getItems().clear();
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
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                clearInterface("Error de conexion con la base de datos", "Error de conexion con la base de datos", "Error de conexion con la base de datos");
            } else if (sqlState != null && sqlState.equals("42000")) {
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                clearInterface("Error de Base de datos desconocida", "Error de Base de datos desconocida", "Error de Base de datos desconocida");
            } else if (sqlState != null && sqlState.equals("28000")) {
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                clearInterface("Error de Acceso denegado", "Error de Acceso denegado", "Error de Acceso denegado");
            } else if (sqlState != null && sqlState.equals("08S01")) {
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                clearInterface("Error de Conexión interrumpida", "Error de Conexión interrumpida", "Error de Conexión interrumpida");
            } else {
                LOGGER.error("No se pudo abrir la evidencia: {}", e.getMessage(), e);
                clearInterface("Error de base de datos al abrir evidencia", "Error de base de datos al abrir evidencia", "Error de base de datos al abrir evidencia");
            }
        } catch (NullPointerException e) {
            LOGGER.error("No se encontró evidencia para el id proporcionado: {} - {}", evidenceId, e.getMessage(), e);
            clearInterface("Evidencia no encontrada", "Evidencia no encontrada", "Evidencia no encontrada");
        } catch (URISyntaxException e) {
            LOGGER.error("URL inválida para abrir evidencia: {}", e.getMessage(), e);
            clearInterface("URL inválida al abrir evidencia", "URL inválida al abrir evidencia", "URL inválida al abrir evidencia");
        } catch (IOException e) {
            LOGGER.error("Error al intentar abrir la evidencia: {}", e.getMessage(), e);
            clearInterface("Error al abrir evidencia", "Error al abrir evidencia", "Error al abrir evidencia");
        } catch (Exception e) {
            LOGGER.error("No se pudo abrir la evidencia: {}", e.getMessage(), e);
            clearInterface("Error inesperado al abrir evidencia", "Error inesperado al abrir evidencia", "Error inesperado al abrir evidencia");
        }
    }
}