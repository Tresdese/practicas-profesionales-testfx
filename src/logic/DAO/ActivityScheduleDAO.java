package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import data_access.ConnectionDataBase;
import logic.DTO.ActivityScheduleDTO;
import logic.interfaces.IActivityScheduleDAO;

public class ActivityScheduleDAO implements IActivityScheduleDAO {
    private final static String SQL_INSERT = "INSERT INTO cronograma_actividad (idCronograma, idActividad) VALUES (?, ?)";
    private final static String SQL_UPDATE = "UPDATE cronograma_actividad SET idCronograma = ?, idActividad = ? WHERE idCronograma = ? AND idActividad = ?";
    private final static String SQL_DELETE = "DELETE FROM cronograma_actividad WHERE idCronograma = ? AND idActividad = ?";
    private final static String SQL_SELECT = "SELECT * FROM cronograma_actividad WHERE idCronograma = ? AND idActividad = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM cronograma_actividad";

    public boolean insertActivitySchedule(ActivityScheduleDTO activitySchedule) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setInt(1, activitySchedule.getIdSchedule());
            statement.setInt(2, activitySchedule.getIdActivity());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateActivitySchedule(ActivityScheduleDTO oldActivitySchedule, ActivityScheduleDTO newActivitySchedule) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setInt(1, newActivitySchedule.getIdSchedule());
            statement.setInt(2, newActivitySchedule.getIdActivity());
            statement.setInt(3, oldActivitySchedule.getIdSchedule());
            statement.setInt(4, oldActivitySchedule.getIdActivity());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteActivitySchedule(ActivityScheduleDTO activitySchedule) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setInt(1, activitySchedule.getIdSchedule());
            statement.setInt(2, activitySchedule.getIdActivity());
            return statement.executeUpdate() > 0;
        }
    }

    public ActivityScheduleDTO searchActivityScheduleByIdScheduleAndIdActivity(ActivityScheduleDTO activitySchedule) throws SQLException {
        ActivityScheduleDTO activityScheduleDTO = new ActivityScheduleDTO(-1, -1);
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setInt(1, activitySchedule.getIdSchedule());
            statement.setInt(2, activitySchedule.getIdActivity());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    activityScheduleDTO = new ActivityScheduleDTO(resultSet.getInt("idCronograma"), resultSet.getInt("idActividad"));
                }
            }
        }
        return activityScheduleDTO;
    }

    public List<ActivityScheduleDTO> getAllActivitySchedules() throws SQLException {
        List<ActivityScheduleDTO> activitySchedules = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                activitySchedules.add(new ActivityScheduleDTO(resultSet.getInt("idCronograma"), resultSet.getInt("idActividad")));
            }
        }
        return activitySchedules;
    }
}
