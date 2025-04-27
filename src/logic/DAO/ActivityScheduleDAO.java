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
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, activitySchedule.getIdSchedule());
            statement.setString(2, activitySchedule.getIdActivity());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateActivitySchedule(ActivityScheduleDTO oldActivitySchedule, ActivityScheduleDTO newActivitySchedule, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, newActivitySchedule.getIdSchedule());
            statement.setString(2, newActivitySchedule.getIdActivity());
            statement.setString(3, oldActivitySchedule.getIdSchedule());
            statement.setString(4, oldActivitySchedule.getIdActivity());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteActivitySchedule(ActivityScheduleDTO activitySchedule, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, activitySchedule.getIdSchedule());
            statement.setString(2, activitySchedule.getIdActivity());
            return statement.executeUpdate() > 0;
        }
    }

    public ActivityScheduleDTO searchActivityScheduleByIdScheduleAndIdActivity(ActivityScheduleDTO activitySchedule, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, activitySchedule.getIdSchedule());
            statement.setString(2, activitySchedule.getIdActivity());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new ActivityScheduleDTO(resultSet.getString("idHorario"), resultSet.getString("idActividad"));
                }
            }
        }
        return null;
    }

    public List<ActivityScheduleDTO> getAllActivitySchedules(Connection connection) throws SQLException {
        List<ActivityScheduleDTO> activitySchedules = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                activitySchedules.add(new ActivityScheduleDTO(resultSet.getString("idHorario"), resultSet.getString("idActividad")));
            }
        }
        return activitySchedules;
    }
}
