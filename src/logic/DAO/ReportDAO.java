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
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
            ps.setString(1, report.getNumberReport());
            ps.setString(2, report.getObservations());
            ps.setString(3, report.getIdEvidence());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateReport(ReportDTO report, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, report.getObservations());
            ps.setString(2, report.getIdEvidence());
            ps.setString(3, report.getNumberReport());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteReport(String numberReport, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setString(1, numberReport);
            return ps.executeUpdate() > 0;
        }
    }

    public ReportDTO getReport(String numberReport, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ps.setString(1, numberReport);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ReportDTO(rs.getString("numReporte"), rs.getString("observaciones"), rs.getString("idEvidencia"));
                }
            }
        }
        return null;
    }

    public List<ReportDTO> getAllReports(Connection connection) throws SQLException {
        List<ReportDTO> reports = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                reports.add(new ReportDTO(rs.getString("numReporte"), rs.getString("observaciones"), rs.getString("idEvidencia")));
            }
        }
        return reports;
    }

}
