package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import logic.DAO.ActivityScheduleDAO;
import logic.DAO.ActivityDAO;
import logic.DAO.ScheduleOfActivitiesDAO;
import logic.DTO.ActivityScheduleDTO;
import logic.DTO.ActivityDTO;
import logic.DTO.ScheduleOfActivitiesDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class GUI_LinkActivityToScheduleController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(GUI_LinkActivityToScheduleController.class);

    @FXML
    private ChoiceBox<ScheduleOfActivitiesDTO> scheduleChoiceBox;

    @FXML
    private ChoiceBox<ActivityDTO> activityChoiceBox;

    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initializeScheduleBox();
            initializeActivityBox();
        }catch (Exception e) {
            statusLabel.setText("Error al cargar datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al inicializar: {}", e.getMessage(), e);
        }
    }

    private void initializeScheduleBox() {
        ScheduleOfActivitiesDAO scheduleDAO = new ScheduleOfActivitiesDAO();
        try {
            List<ScheduleOfActivitiesDTO> schedules = scheduleDAO.getAllSchedulesOfActivities();
            scheduleChoiceBox.setItems(FXCollections.observableArrayList(schedules));
            scheduleChoiceBox.setConverter(new StringConverter<ScheduleOfActivitiesDTO>() {
                @Override
                public String toString(ScheduleOfActivitiesDTO object) {
                    if (object == null) return "";
                    try {
                        ScheduleOfActivitiesDTO schedule = scheduleDAO.searchScheduleOfActivitiesById(object.getIdSchedule());
                        return schedule.getMilestone();
                    } catch (Exception e) {
                        return "";
                    }
                }
                @Override
                public ScheduleOfActivitiesDTO fromString(String string) {
                    throw new UnsupportedOperationException("Conversión desde String a Cronograma de actividades no soportada.");
                }
            });
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error base de datos al cargar cronogramas.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error base de datos al cargar cronogramas: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar cronogramas.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al cargar cronogramas: {}", e.getMessage(), e);
        }
    }

    private void initializeActivityBox() {
        try {
            ActivityDAO activityDAO = new ActivityDAO();
            List<ActivityDTO> activities = activityDAO.getAllActivities();
            activityChoiceBox.setItems(FXCollections.observableArrayList(activities));
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar actividades.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al cargar actividades: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar actividades.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al cargar actividades: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleLinkActivityToSchedule() {
        try {
            ScheduleOfActivitiesDTO selectedSchedule = scheduleChoiceBox.getValue();
            ActivityDTO selectedActivity = activityChoiceBox.getValue();

            if (selectedSchedule == null || selectedActivity == null) {
                throw new IllegalArgumentException("Debe seleccionar un cronograma y una actividad.");
            }

            int idSchedule = Integer.parseInt(selectedSchedule.getIdSchedule());
            int idActivity = Integer.parseInt(selectedActivity.getActivityId());

            ActivityScheduleDTO activitySchedule = new ActivityScheduleDTO(idSchedule, idActivity);

            ActivityScheduleDAO activityScheduleDAO = new ActivityScheduleDAO();
            boolean success = activityScheduleDAO.insertActivitySchedule(activitySchedule);

            if (success) {
                statusLabel.setText("¡Actividad vinculada exitosamente!");
                statusLabel.setTextFill(Color.GREEN);
            } else {
                throw new SQLException("No se pudo vincular la actividad.");
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("23000")) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al vincular la actividad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al vincular la actividad: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al vincular la actividad.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado: {}", e.getMessage(), e);
        }
    }
}