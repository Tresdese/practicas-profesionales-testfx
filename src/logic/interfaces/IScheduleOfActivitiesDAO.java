package logic.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.ScheduleOfActivitiesDTO;

public interface IScheduleOfActivitiesDAO {
    boolean insertScheduleOfActivities(ScheduleOfActivitiesDTO schedule, Connection connection) throws SQLException;

    boolean updateScheduleOfActivities(ScheduleOfActivitiesDTO schedule, Connection connection) throws SQLException;

    boolean deleteScheduleOfActivities(ScheduleOfActivitiesDTO schedule, Connection connection) throws SQLException;

    ScheduleOfActivitiesDTO searchScheduleOfActivitiesById(String idSchedule, Connection connection) throws SQLException;

    List<ScheduleOfActivitiesDTO> getAllSchedulesOfActivities(Connection connection) throws SQLException;
}
