package logic.DAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import data_access.ConnectionDataBase;
import logic.DTO.ActivityReportDTO;
import logic.interfaces.IActivityReportDAO;

public class ActivityReportDAO implements IActivityReportDAO {

    private final static String SQL_INSERT = "INSERT INTO reporte_actividad (numReporte, idActividad, porcentaje_avance, observaciones) VALUES (?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE reporte_actividad SET idActividad = ?, porcentaje_avance = ?, observaciones = ? WHERE numReporte = ?";
    private final static String SQL_DELETE = "DELETE FROM reporte_actividad WHERE numReporte = ?";
    private final static String SQL_SELECT = "SELECT * FROM reporte_actividad WHERE numReporte = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM reporte_actividad";

    public boolean insertActivityReport(ActivityReportDTO activityReport) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, activityReport.getNumberReport());
            statement.setString(2, activityReport.getIdActivity());
            statement.setInt(3, activityReport.getProgressPercentage());
            statement.setString(4, activityReport.getObservations());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateActivityReport(ActivityReportDTO activityReport) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, activityReport.getIdActivity());
            statement.setInt(2, activityReport.getProgressPercentage());
            statement.setString(3, activityReport.getObservations());
            statement.setString(4, activityReport.getNumberReport());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteActivityReport(String reportNumber) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, reportNumber);
            return statement.executeUpdate() > 0;
        }
    }

    public ActivityReportDTO searchActivityReportByReportNumber(String reportNumber) throws SQLException, IOException {
        ActivityReportDTO activityReport = new ActivityReportDTO("N/A", "N/A", 0, "");
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, reportNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    activityReport = new ActivityReportDTO(
                            resultSet.getString("numReporte"),
                            resultSet.getString("idActividad"),
                            resultSet.getInt("porcentaje_avance"),
                            resultSet.getString("observaciones")
                    );
                }
            }
        }
        return activityReport;
    }

    public List<ActivityReportDTO> getAllActivityReports() throws SQLException, IOException {
        List<ActivityReportDTO> activityReports = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                activityReports.add(new ActivityReportDTO(
                        resultSet.getString("numReporte"),
                        resultSet.getString("idActividad"),
                        resultSet.getInt("porcentaje_avance"),
                        resultSet.getString("observaciones")
                ));
            }
        }
        return activityReports;
    }
}