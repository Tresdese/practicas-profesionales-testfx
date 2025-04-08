package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.ScheduleOfActivitiesDTO;
import logic.interfaces.IScheduleOfActivitiesDAO;

public class ScheduleOfActivitiesDAO implements IScheduleOfActivitiesDAO {
    private final static String SQL_INSERT = "INSERT INTO cronograma_de_actividades (idCronograma, hito, fechaEstimada, matricula, idEvidencia) VALUES (?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE cronograma_de_actividades SET hito = ?, fechaEstimada = ?, matricula = ?, idEvidencia = ? WHERE idCronograma = ?";
    private final static String SQL_DELETE = "DELETE FROM cronograma_de_actividades WHERE idCronograma = ?";
    private final static String SQL_SELECT = "SELECT * FROM cronograma_de_actividades WHERE idCronograma = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM cronograma_de_actividades";

    public boolean insertScheduleOfActivities(ScheduleOfActivitiesDTO schedule, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
            ps.setString(1, schedule.getIdSchedule());
            ps.setString(2, schedule.getMilestone());
            ps.setTimestamp(3, schedule.getEstimatedDate());
            ps.setString(4, schedule.getTuiton());
            ps.setString(5, schedule.getIdEvidence());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateScheduleOfActivities(ScheduleOfActivitiesDTO schedule, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, schedule.getMilestone());
            ps.setTimestamp(2, schedule.getEstimatedDate());
            ps.setString(3, schedule.getTuiton());
            ps.setString(4, schedule.getIdEvidence());
            ps.setString(5, schedule.getIdSchedule());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteScheduleOfActivities(ScheduleOfActivitiesDTO schedule, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setString(1, schedule.getIdSchedule());
            return ps.executeUpdate() > 0;
        }
    }

    public ScheduleOfActivitiesDTO getScheduleOfActivities(String idSchedule, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ps.setString(1, idSchedule);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ScheduleOfActivitiesDTO(
                        rs.getString("idCronograma"),
                        rs.getString("hito"),
                        rs.getTimestamp("fechaEstimada"),
                        rs.getString("matricula"),
                        rs.getString("idEvidencia")
                    );
                }
            }
        }
        return null;
    }

    public List<ScheduleOfActivitiesDTO> getAllSchedulesOfActivities(Connection connection) throws SQLException {
        List<ScheduleOfActivitiesDTO> schedules = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                schedules.add(new ScheduleOfActivitiesDTO(
                    rs.getString("idCronograma"),
                    rs.getString("hito"),
                    rs.getTimestamp("fechaEstimada"),
                    rs.getString("matricula"),
                    rs.getString("idEvidencia")
                ));
            }
        }
        return schedules;
    }
}
