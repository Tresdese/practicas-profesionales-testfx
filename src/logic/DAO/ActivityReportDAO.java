package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.ActivityReportDTO;

public class ActivityReportDAO {
    private final static String SQL_INSERT = "INSERT INTO reporte (numReporte, idActividad) VALUES (?, ?)";
    private final static String SQL_UPDATE = "UPDATE reporte SET idActividad = ? WHERE numReporte = ?";
    private final static String SQL_DELETE = "DELETE FROM reporte WHERE numReporte = ?";
    private final static String SQL_SELECT = "SELECT * FROM reporte WHERE numReporte = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM reporte";

    public boolean insertActivityReport(ActivityReportDTO activityReport, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
            ps.setString(1, activityReport.getNumberReport());
            ps.setString(2, activityReport.getIdActivity());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateActivityReport(ActivityReportDTO activityReport, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, activityReport.getIdActivity());
            ps.setString(2, activityReport.getNumberReport());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteActivityReport(String reportNumber, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setString(1, reportNumber);
            return ps.executeUpdate() > 0;
        }
    }

    public ActivityReportDTO getActivityReport(String reportNumber, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ps.setString(1, reportNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ActivityReportDTO(rs.getString("numReporte"), rs.getString("idActividad"));
                }
            }
        }
        return null;
    }

    public List<ActivityReportDTO> getAllActivityReports(Connection connection) throws SQLException {
        List<ActivityReportDTO> activityReports = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                activityReports.add(new ActivityReportDTO(rs.getString("numReporte"), rs.getString("idActividad")));
            }
        }
        return activityReports;
    }
}
