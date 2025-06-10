package logic.interfaces;

import logic.DTO.ActivityScheduleDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IActivityScheduleDAO {
    boolean insertActivitySchedule(ActivityScheduleDTO activitySchedule) throws SQLException;
    boolean updateActivitySchedule(ActivityScheduleDTO oldActivitySchedule, ActivityScheduleDTO newActivitySchedule) throws SQLException;
    boolean deleteActivitySchedule(ActivityScheduleDTO activitySchedule) throws SQLException;
    ActivityScheduleDTO searchActivityScheduleByIdScheduleAndIdActivity(ActivityScheduleDTO activitySchedule) throws SQLException;
    List<ActivityScheduleDTO> getAllActivitySchedules() throws SQLException;
}
