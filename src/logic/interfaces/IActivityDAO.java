package logic.interfaces;

import logic.DTO.ActivityDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IActivityDAO {

    boolean insertActivity(ActivityDTO activity) throws SQLException;

    boolean updateActivity(ActivityDTO activity) throws SQLException;

    boolean deleteActivity(ActivityDTO activity) throws SQLException;

    ActivityDTO searchActivityById(String idActivity) throws SQLException;

    List<ActivityDTO> getAllActivities() throws SQLException;
}