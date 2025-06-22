package logic.interfaces;

import logic.DTO.ActivityScheduleDTO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IActivityScheduleDAO {
    boolean insertActivitySchedule(ActivityScheduleDTO activitySchedule) throws SQLException, IOException;
    boolean updateActivitySchedule(ActivityScheduleDTO oldActivitySchedule, ActivityScheduleDTO newActivitySchedule) throws SQLException, IOException;
    boolean deleteActivitySchedule(ActivityScheduleDTO activitySchedule) throws SQLException, IOException;
    ActivityScheduleDTO searchActivityScheduleByIdScheduleAndIdActivity(ActivityScheduleDTO activitySchedule) throws SQLException, IOException;
    List<ActivityScheduleDTO> getAllActivitySchedules() throws SQLException, IOException;
}
