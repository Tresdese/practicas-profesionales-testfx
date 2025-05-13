package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.ActivityDTO;
import logic.interfaces.IActivityDAO;

public class ActivityDAO implements IActivityDAO { // TODO implementar la interfaz IActivityDAO
    private final Connection connection;

    private final static String SQL_INSERT = "INSERT INTO actividad (idActividad, nombreActividad) VALUES (?, ?)";
    private final static String SQL_UPDATE = "UPDATE actividad SET nombreActividad = ? WHERE idActividad = ?";
    private final static String SQL_DELETE = "DELETE FROM actividad WHERE idActividad = ?";
    private final static String SQL_SELECT_BY_NAME = "SELECT idActividad FROM actividad WHERE nombreActividad = ?";
    private final static String SQL_SELECT = "SELECT * FROM actividad WHERE idActividad = ?";

    public ActivityDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean insertActivity(ActivityDTO activity) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, activity.getActivityId());
            statement.setString(2, activity.getActivityName());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateActivity(ActivityDTO activity) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, activity.getActivityName());
            statement.setString(2, activity.getActivityId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteActivity(ActivityDTO activity) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, activity.getActivityId());
            return statement.executeUpdate() > 0;
        }
    }

    public ActivityDTO searchActivityById(String idActivity) throws SQLException {
        ActivityDTO activity = new ActivityDTO("invalido","invalido");
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, idActivity);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    activity = new ActivityDTO (resultSet.getString("idActividad"), resultSet.getString("nombreActividad"));
                }
            }
        }
        return activity;
    }

    public int getActivityByName (String name) throws SQLException {
        int id = -1;
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_NAME)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    id = resultSet.getInt("idActividad");
                }
            }
        }
        return id;
    }

    public List<ActivityDTO> getAllActivities() throws SQLException {
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