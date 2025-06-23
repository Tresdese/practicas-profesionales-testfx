package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import logic.DAO.EvidenceDAO;
import logic.DAO.ScheduleOfActivitiesDAO;
import logic.DTO.ScheduleOfActivitiesDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

public class GUI_CheckScheduleOfActivitiesController {
    private static final Logger LOGGER = LogManager.getLogger(GUI_CheckScheduleOfActivitiesController.class);

    @FXML
    private ListView<String> scheduleListView;
    @FXML
    private Label statusLabel;
    @FXML
    private Label milestoneLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label evidenceLabel;
    @FXML
    private Button viewEvidenceButton;

    private List<ScheduleOfActivitiesDTO> schedules;
    private int selectedIndex = -1;

    private String studentTuition;

    public void setStudentTuition(String tuition) {
        this.studentTuition = tuition;
        loadScheduleOfActivities();
    }

    private void loadScheduleOfActivities() {
        try {
            ScheduleOfActivitiesDAO dao = new ScheduleOfActivitiesDAO();
            schedules = dao.getAllSchedulesOfActivities()
                    .stream()
                    .filter(s -> s.getTuition().equalsIgnoreCase(studentTuition))
                    .toList();

            scheduleListView.getItems().clear();

            if (schedules.isEmpty()) {
                statusLabel.setText("No hay cronograma registrado para este estudiante.");
                clearLabels();
                viewEvidenceButton.setDisable(true);
            } else {
                for (ScheduleOfActivitiesDTO schedule : schedules) {
                    scheduleListView.getItems().add(
                            "Hito: " + schedule.getMilestone() +
                                    " | Fecha estimada: " + schedule.getEstimatedDate() +
                                    " | Evidencia: " + schedule.getIdEvidence()
                    );
                }
                statusLabel.setText("");
                scheduleListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> showScheduleDetails(newVal.intValue()));
                scheduleListView.getSelectionModel().selectFirst();
                showScheduleDetails(0);
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                LOGGER.error("Error de conexión a la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Error de conexión a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                clearLabels();
                viewEvidenceButton.setDisable(true);
            } else if ("08S01".equals(sqlState)) {
                LOGGER.error("Error de conexión interrumpida a la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Conexión interrumpida a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                clearLabels();
                viewEvidenceButton.setDisable(true);
            } else if ("42000".equals(sqlState)) {
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                clearLabels();
                viewEvidenceButton.setDisable(true);
            } else if ("42S02".equals(sqlState)) {
                LOGGER.error("Tabla de cronograma no encontrada: {}", e.getMessage(), e);
                statusLabel.setText("Tabla de cronograma no encontrada.");
                statusLabel.setTextFill(Color.RED);
                clearLabels();
                viewEvidenceButton.setDisable(true);
            } else if ("22007".equals(sqlState)) {
                LOGGER.error("Formato de fecha inválido: {}", e.getMessage(), e);
                statusLabel.setText("Formato de fecha inválido en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                clearLabels();
                viewEvidenceButton.setDisable(true);
            } else if ("23000".equals(sqlState)) {
                LOGGER.error("Violación de restricción de clave foránea: {}", e.getMessage(), e);
                statusLabel.setText("Violación de restricción de clave foránea al obtener el cronograma.");
                statusLabel.setTextFill(Color.RED);
                clearLabels();
                viewEvidenceButton.setDisable(true);
            } else if ("42S22".equals(sqlState)) {
                LOGGER.error("Columna no encontrada en la tabla del cronograma: {}", e.getMessage(), e);
                statusLabel.setText("Columna no encontrada en la tabla del cronograma.");
                statusLabel.setTextFill(Color.RED);
                clearLabels();
                viewEvidenceButton.setDisable(true);
            } else if ("HY000".equals(sqlState)) {
                LOGGER.error("Error general de SQL: {}", e.getMessage(), e);
                statusLabel.setText("Error general de SQL al obtener el cronograma.");
                statusLabel.setTextFill(Color.RED);
                clearLabels();
                viewEvidenceButton.setDisable(true);
            } else {
                LOGGER.error("Error SQL al obtener el cronograma: {}", e.getMessage(), e);
                statusLabel.setText("Error al obtener el cronograma desde la base de datos.");
                statusLabel.setTextFill(Color.RED);
                clearLabels();
                viewEvidenceButton.setDisable(true);
            }
        } catch (IOException e) {
            LOGGER.error("Error al leer la configuración de la base de datos: {}", e.getMessage(), e);
            statusLabel.setText("Error al leer la configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            clearLabels();
            viewEvidenceButton.setDisable(true);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al obtener el cronograma: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al obtener el cronograma.");
            statusLabel.setTextFill(Color.RED);
            clearLabels();
            viewEvidenceButton.setDisable(true);
        }
    }

    private void showScheduleDetails(int index) {
        if (index < 0 || schedules == null || schedules.isEmpty()) {
            clearLabels();
            viewEvidenceButton.setDisable(true);
            selectedIndex = -1;
            return;
        }
        ScheduleOfActivitiesDTO schedule = schedules.get(index);
        milestoneLabel.setText(schedule.getMilestone());
        dateLabel.setText(String.valueOf(schedule.getEstimatedDate()));
        evidenceLabel.setText(schedule.getIdEvidence());
        viewEvidenceButton.setDisable(schedule.getIdEvidence() == null || schedule.getIdEvidence().isEmpty());
        selectedIndex = index;
    }

    private void clearLabels() {
        milestoneLabel.setText("-");
        dateLabel.setText("-");
        evidenceLabel.setText("-");
    }

    @FXML
    private void handleViewEvidence() {
        if (selectedIndex < 0 || schedules == null || schedules.isEmpty()) {
            return;
        }
        String idEvidence = schedules.get(selectedIndex).getIdEvidence();
        if (idEvidence == null || idEvidence.isEmpty()) {
            statusLabel.setText("No hay evidencia asociada.");
            return;
        }
        try {
            EvidenceDAO evidenceDAO = new EvidenceDAO();
            String url = evidenceDAO.searchEvidenceById(Integer.parseInt(idEvidence)).getRoute();
            if (url != null && !url.isEmpty()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI(url));
                } else {
                    LOGGER.error("Desktop no soportado para abrir URLs.");
                    statusLabel.setText("No se puede abrir la evidencia en este sistema.");
                    statusLabel.setTextFill(Color.RED);
                }
            } else {
                statusLabel.setText("No se encontró URL de evidencia.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.warn("No se encontró URL de evidencia para ID: {}", idEvidence);
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                LOGGER.error("Error de conexión a la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Error de conexión a la base de datos.");
                statusLabel.setTextFill(Color.RED);
            } else if ("08S01".equals(sqlState)) {
                LOGGER.error("Error de conexion interrumpida a la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Conexión interrumpida a la base de datos.");
                statusLabel.setTextFill(Color.RED);
            } else if ("42000".equals(sqlState)) {
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
            } else if ("42S02".equals(sqlState)) {
                LOGGER.error("Tabla de evidencia no encontrada: {}", e.getMessage(), e);
                statusLabel.setText("Tabla de evidencia no encontrada.");
                statusLabel.setTextFill(Color.RED);
            } else if ("22007".equals(sqlState)) {
                LOGGER.error("Formato de fecha inválido: {}", e.getMessage(), e);
                statusLabel.setText("Formato de fecha inválido en la base de datos.");
                statusLabel.setTextFill(Color.RED);
            } else if ("23000".equals(sqlState)) {
                LOGGER.error("Violación de restricción de clave foránea: {}", e.getMessage(), e);
                statusLabel.setText("Violación de restricción de clave foránea al buscar evidencia.");
                statusLabel.setTextFill(Color.RED);
            } else if ("42S22".equals(sqlState)) {
                LOGGER.error("Columna no encontrada en la tabla de evidencia: {}", e.getMessage(), e);
                statusLabel.setText("Columna no encontrada en la tabla de evidencia.");
                statusLabel.setTextFill(Color.RED);
            } else if ("HY000".equals(sqlState)) {
                LOGGER.error("Error general de SQL: {}", e.getMessage(), e);
                statusLabel.setText("Error general de SQL al buscar evidencia.");
                statusLabel.setTextFill(Color.RED);
            } else {
                LOGGER.error("Error SQL al buscar evidencia: {}", e.getMessage(), e);
                statusLabel.setText("Error al buscar evidencia en la base de datos.");
                statusLabel.setTextFill(Color.RED);
            }
        } catch (NumberFormatException e) {
            LOGGER.error("ID de evidencia inválido: {}", e.getMessage(), e);
            statusLabel.setText("ID de evidencia inválido.");
        } catch (IOException e) {
            LOGGER.error("Error al leer el archivo de configuracion de la base de datos : {}", e.getMessage(), e);
            statusLabel.setText("Error al leer el archivo de configuración de la base de datos.");
        } catch (URISyntaxException e) {
            LOGGER.error("URL inválida para evidencia: {}", e.getMessage(), e);
            statusLabel.setText("URL inválida para evidencia.");
        } catch (Exception e) {
            LOGGER.error("Error inesperado al abrir la evidencia: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al abrir la evidencia.");
        }
    }
}