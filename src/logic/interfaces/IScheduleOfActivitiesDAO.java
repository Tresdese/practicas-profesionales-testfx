package logic.interfaces;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.ScheduleOfActivitiesDTO;

public interface IScheduleOfActivitiesDAO {
    boolean insertScheduleOfActivities(ScheduleOfActivitiesDTO schedule) throws SQLException, IOException;

    boolean updateScheduleOfActivities(ScheduleOfActivitiesDTO schedule) throws SQLException, IOException;

    boolean deleteScheduleOfActivities(ScheduleOfActivitiesDTO schedule) throws SQLException, IOException;

    ScheduleOfActivitiesDTO searchScheduleOfActivitiesById(String idSchedule) throws SQLException, IOException;

    List<ScheduleOfActivitiesDTO> getAllSchedulesOfActivities() throws SQLException, IOException;
}
