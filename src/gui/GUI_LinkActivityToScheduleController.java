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

    private static final Logger logger = LogManager.getLogger(GUI_LinkActivityToScheduleController.class);

    @FXML
    private ChoiceBox<ScheduleOfActivitiesDTO> scheduleBox;

    @FXML
    private ChoiceBox<ActivityDTO> activityBox;

    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initializeScheduleBox();
            initializeActivityBox();
        } catch (SQLException e) {
            statusLabel.setText("Error al cargar cronogramas o actividades.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error al inicializar: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error al cargar datos.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error al inicializar: {}", e.getMessage(), e);
        }
    }

    private void initializeScheduleBox() throws SQLException {
        ScheduleOfActivitiesDAO scheduleDAO = new ScheduleOfActivitiesDAO();
        List<ScheduleOfActivitiesDTO> schedules = scheduleDAO.getAllSchedulesOfActivities();
        scheduleBox.setItems(FXCollections.observableArrayList(schedules));
        scheduleBox.setConverter(new StringConverter<ScheduleOfActivitiesDTO>() {
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
                return null;
            }
        });
    }

    private void initializeActivityBox() throws SQLException {
        ActivityDAO activityDAO = new ActivityDAO();
        List<ActivityDTO> activities = activityDAO.getAllActivities();
        activityBox.setItems(FXCollections.observableArrayList(activities));
    }

    @FXML
    private void handleLinkActivityToSchedule() {
        try {
            ScheduleOfActivitiesDTO selectedSchedule = scheduleBox.getValue();
            ActivityDTO selectedActivity = activityBox.getValue();

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
            statusLabel.setText("Error al vincular la actividad: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
            logger.error("Error: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al vincular la actividad.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error inesperado: {}", e.getMessage(), e);
        }
    }
}