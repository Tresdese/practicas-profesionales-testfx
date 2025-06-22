package logic.interfaces;

import logic.DTO.ActivityDTO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IActivityDAO {

    boolean insertActivity(ActivityDTO activity) throws SQLException, IOException;

    boolean updateActivity(ActivityDTO activity) throws SQLException, IOException;

    boolean deleteActivity(ActivityDTO activity) throws SQLException, IOException;

    ActivityDTO searchActivityById(String idActivity) throws SQLException, IOException;

    int getActivityByName (String name) throws SQLException, IOException;

    List<ActivityDTO> getAllActivities() throws SQLException, IOException;
}