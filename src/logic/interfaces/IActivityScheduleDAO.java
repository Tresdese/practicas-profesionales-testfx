package logic.interfaces;

import logic.DTO.ActivityScheduleDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IActivityScheduleDAO {
    boolean insertActivitySchedule(ActivityScheduleDTO activitySchedule, Connection connection) throws SQLException;

    boolean updateActivitySchedule(ActivityScheduleDTO oldActivitySchedule, ActivityScheduleDTO newActivitySchedule, Connection connection) throws SQLException;

    boolean deleteActivitySchedule(ActivityScheduleDTO activitySchedule, Connection connection) throws SQLException;

    ActivityScheduleDTO searchActivityScheduleByIdScheduleAndIdActivity(ActivityScheduleDTO activitySchedule, Connection connection) throws SQLException;

    List<ActivityScheduleDTO> getAllActivitySchedules(Connection connection) throws SQLException;
}
