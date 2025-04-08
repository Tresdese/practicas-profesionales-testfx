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
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
            ps.setString(1, activity.getActivityId());
            ps.setString(2, activity.getActivityName());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateActivity(ActivityDTO activity, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, activity.getActivityName());
            ps.setString(2, activity.getActivityId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteActivity(ActivityDTO activity, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setString(1, activity.getActivityId());
            return ps.executeUpdate() > 0;
        }
    }

    public ActivityDTO getActivity(String idActivity, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ps.setString(1, idActivity);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ActivityDTO(rs.getString("idActividad"), rs.getString("nombreActividad"));
                }
            }
        }
        return null;
    }

    public List<ActivityDTO> getAllActivities(Connection connection) throws SQLException {
        List<ActivityDTO> activities = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT * FROM actividad";
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                activities.add(new ActivityDTO(rs.getString("idActividad"), rs.getString("nombreActividad")));
            }
        }
        return activities;
    }
}