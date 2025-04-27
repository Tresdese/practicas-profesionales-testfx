package logic.interfaces;

import logic.DTO.ActivityDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IActivityDAO {

    boolean insertActivity(ActivityDTO activity, Connection connection) throws SQLException;

    boolean updateActivity(ActivityDTO activity, Connection connection) throws SQLException;

    boolean deleteActivity(ActivityDTO activity, Connection connection) throws SQLException;

    ActivityDTO searchActivityById(String idActivity, Connection connection) throws SQLException;

    List<ActivityDTO> getAllActivities(Connection connection) throws SQLException;
}