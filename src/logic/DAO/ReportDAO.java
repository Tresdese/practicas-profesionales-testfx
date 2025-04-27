package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.ReportDTO;
import logic.interfaces.IReportDAO;

public class ReportDAO implements IReportDAO {
    private final static String SQL_INSERT = "INSERT INTO reporte (numReporte, observaciones, idEvidencia) VALUES (?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE reporte SET observaciones = ?, idEvidencia = ? WHERE numReporte = ?";
    private final static String SQL_DELETE = "DELETE FROM reporte WHERE numReporte = ?";
    private final static String SQL_SELECT = "SELECT * FROM reporte WHERE numReporte = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM reporte";

    public boolean insertReport(ReportDTO report, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, report.getNumberReport());
            statement.setString(2, report.getObservations());
            statement.setString(3, report.getIdEvidence());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateReport(ReportDTO report, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, report.getObservations());
            statement.setString(2, report.getIdEvidence());
            statement.setString(3, report.getNumberReport());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteReport(String numberReport, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, numberReport);
            return statement.executeUpdate() > 0;
        }
    }

    public ReportDTO searchReportById(String numberReport, Connection connection) throws SQLException {
        ReportDTO report = new ReportDTO("N/A", "N/A", "N/A");
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, numberReport);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    report = new ReportDTO(resultSet.getString("numReporte"), resultSet.getString("observaciones"), resultSet.getString("idEvidencia"));
                }
            }
        }
        return report;
    }

    public List<ReportDTO> getAllReports(Connection connection) throws SQLException {
        List<ReportDTO> reports = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                reports.add(new ReportDTO(resultSet.getString("numReporte"), resultSet.getString("observaciones"), resultSet.getString("idEvidencia")));
            }
        }
        return reports;
    }

    //TODO hacer metodo que verifique si existe el reporte
}
