package logic.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.ScheduleOfActivitiesDTO;

public interface IScheduleOfActivitiesDAO {
    boolean insertScheduleOfActivities(ScheduleOfActivitiesDTO schedule) throws SQLException;

    boolean updateScheduleOfActivities(ScheduleOfActivitiesDTO schedule) throws SQLException;

    boolean deleteScheduleOfActivities(ScheduleOfActivitiesDTO schedule) throws SQLException;

    ScheduleOfActivitiesDTO searchScheduleOfActivitiesById(String idSchedule) throws SQLException;

    List<ScheduleOfActivitiesDTO> getAllSchedulesOfActivities() throws SQLException;
}
