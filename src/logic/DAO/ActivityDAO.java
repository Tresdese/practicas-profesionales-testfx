package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.ActivityDTO;
import logic.interfaces.IActivityDAO;

public class ActivityDAO implements IActivityDAO {
    private final static String SQL_INSERT = "INSERT INTO actividad (idActividad, nombreActividad) VALUES (?, ?)";
    private final static String SQL_UPDATE = "UPDATE actividad SET nombreActividad = ? WHERE idActividad = ?";
    private final static String SQL_DELETE = "DELETE FROM actividad WHERE idActividad = ?";
    private final static String SQL_SELECT = "SELECT * FROM actividad WHERE idActividad = ?";

    public boolean insertActivity(ActivityDTO activity, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, activity.getActivityId());
            statement.setString(2, activity.getActivityName());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateActivity(ActivityDTO activity, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, activity.getActivityName());
            statement.setString(2, activity.getActivityId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteActivity(ActivityDTO activity, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, activity.getActivityId());
            return statement.executeUpdate() > 0;
        }
    }

    public ActivityDTO getActivity(String idActivity, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, idActivity);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new ActivityDTO(resultSet.getString("idActividad"), resultSet.getString("nombreActividad"));
                }
            }
        }
        return null;
    }

    public List<ActivityDTO> getAllActivities(Connection connection) throws SQLException {
        List<ActivityDTO> activities = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT * FROM actividad";
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                activities.add(new ActivityDTO(resultSet.getString("idActividad"), resultSet.getString("nombreActividad")));
            }
        }
        return activities;
    }
}