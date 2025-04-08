package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.ActivityScheduleDTO;
import logic.interfaces.IActivityScheduleDAO;

public class ActivityScheduleDAO implements IActivityScheduleDAO {
    private final static String SQL_INSERT = "INSERT INTO actividad_programada (idHorario, idActividad) VALUES (?, ?)";
    private final static String SQL_UPDATE = "UPDATE actividad_programada SET idHorario = ?, idActividad = ? WHERE idHorario = ? AND idActividad = ?";
    private final static String SQL_DELETE = "DELETE FROM actividad_programada WHERE idHorario = ? AND idActividad = ?";
    private final static String SQL_SELECT = "SELECT * FROM actividad_programada WHERE idHorario = ? AND idActividad = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM actividad_programada";

    public boolean insertActivitySchedule(ActivityScheduleDTO activitySchedule, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
            ps.setString(1, activitySchedule.getIdSchedule());
            ps.setString(2, activitySchedule.getIdActivity());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateActivitySchedule(ActivityScheduleDTO oldActivitySchedule, ActivityScheduleDTO newActivitySchedule, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, newActivitySchedule.getIdSchedule());
            ps.setString(2, newActivitySchedule.getIdActivity());
            ps.setString(3, oldActivitySchedule.getIdSchedule());
            ps.setString(4, oldActivitySchedule.getIdActivity());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteActivitySchedule(ActivityScheduleDTO activitySchedule, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setString(1, activitySchedule.getIdSchedule());
            ps.setString(2, activitySchedule.getIdActivity());
            return ps.executeUpdate() > 0;
        }
    }

    public ActivityScheduleDTO getActivitySchedule(ActivityScheduleDTO activitySchedule, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ps.setString(1, activitySchedule.getIdSchedule());
            ps.setString(2, activitySchedule.getIdActivity());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ActivityScheduleDTO(rs.getString("idHorario"), rs.getString("idActividad"));
                }
            }
        }
        return null;
    }

    public List<ActivityScheduleDTO> getAllActivitySchedules(Connection connection) throws SQLException {
        List<ActivityScheduleDTO> activitySchedules = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                activitySchedules.add(new ActivityScheduleDTO(rs.getString("idHorario"), rs.getString("idActividad")));
            }
        }
        return activitySchedules;
    }
}
