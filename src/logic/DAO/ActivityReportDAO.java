package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.ActivityReportDTO;
import logic.interfaces.IActivityReportDAO;

public class ActivityReportDAO implements IActivityReportDAO {
    private final static String SQL_INSERT = "INSERT INTO reporte (numReporte, idActividad) VALUES (?, ?)";
    private final static String SQL_UPDATE = "UPDATE reporte SET idActividad = ? WHERE numReporte = ?";
    private final static String SQL_DELETE = "DELETE FROM reporte WHERE numReporte = ?";
    private final static String SQL_SELECT = "SELECT * FROM reporte WHERE numReporte = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM reporte";

    public boolean insertActivityReport(ActivityReportDTO activityReport, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, activityReport.getNumberReport());
            statement.setString(2, activityReport.getIdActivity());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateActivityReport(ActivityReportDTO activityReport, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, activityReport.getIdActivity());
            statement.setString(2, activityReport.getNumberReport());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteActivityReport(String reportNumber, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, reportNumber);
            return statement.executeUpdate() > 0;
        }
    }

    public ActivityReportDTO getActivityReport(String reportNumber, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, reportNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new ActivityReportDTO(resultSet.getString("numReporte"), resultSet.getString("idActividad"));
                }
            }
        }
        return null;
    }

    public List<ActivityReportDTO> getAllActivityReports(Connection connection) throws SQLException {
        List<ActivityReportDTO> activityReports = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                activityReports.add(new ActivityReportDTO(resultSet.getString("numReporte"), resultSet.getString("idActividad")));
            }
        }
        return activityReports;
    }
}
