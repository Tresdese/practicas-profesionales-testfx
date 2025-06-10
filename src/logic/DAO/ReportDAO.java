package logic.DAO;

import data_access.ConecctionDataBase;
import logic.DTO.ReportDTO;
import logic.interfaces.IReportDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO implements IReportDAO {

    private static final String SQL_INSERT = "INSERT INTO reporte (fecha_reporte, total_horas, objetivo_general, metodologia, resultado_obtenido, idProyecto, matricula, observaciones, idEvidencia) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE reporte SET fecha_reporte = ?, total_horas = ?, objetivo_general = ?, metodologia = ?, resultado_obtenido = ?, idProyecto = ?, matricula = ?, observaciones = ?, idEvidencia = ? WHERE numReporte = ?";
    private static final String SQL_DELETE = "DELETE FROM reporte WHERE numReporte = ?";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM reporte WHERE numReporte = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM reporte";

    @Override
    public boolean insertReport(ReportDTO report) throws SQLException {
        try (ConecctionDataBase db = new ConecctionDataBase();
             Connection conn = db.connectDB();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, new java.sql.Date(report.getReportDate().getTime()));
            stmt.setInt(2, report.getTotalHours());
            stmt.setString(3, report.getGeneralObjective());
            stmt.setString(4, report.getMethodology());
            stmt.setString(5, report.getObtainedResult());
            stmt.setInt(6, report.getProjectId());
            stmt.setString(7, report.getTuition());
            stmt.setString(8, report.getObservations());
            stmt.setInt(9, Integer.parseInt(report.getIdEvidence()));
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        report.setNumberReport(String.valueOf(generatedKeys.getInt(1)));
                    }
                }
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean updateReport(ReportDTO report) throws SQLException {
        try (ConecctionDataBase db = new ConecctionDataBase();
             Connection conn = db.connectDB();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            stmt.setDate(1, new java.sql.Date(report.getReportDate().getTime()));
            stmt.setInt(2, report.getTotalHours());
            stmt.setString(3, report.getGeneralObjective());
            stmt.setString(4, report.getMethodology());
            stmt.setString(5, report.getObtainedResult());
            stmt.setInt(6, report.getProjectId());
            stmt.setString(7, report.getTuition());
            stmt.setString(8, report.getObservations());
            stmt.setInt(9, Integer.parseInt(report.getIdEvidence()));
            stmt.setInt(10, Integer.parseInt(report.getNumberReport()));
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteReport(String numberReport) throws SQLException {
        try (ConecctionDataBase db = new ConecctionDataBase();
             Connection conn = db.connectDB();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            stmt.setInt(1, Integer.parseInt(numberReport));
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public ReportDTO searchReportById(String numberReport) throws SQLException {
        try (ConecctionDataBase db = new ConecctionDataBase();
             Connection conn = db.connectDB();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            stmt.setInt(1, Integer.parseInt(numberReport));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ReportDTO(
                            String.valueOf(rs.getInt("numReporte")),
                            rs.getDate("fecha_reporte"),
                            rs.getInt("total_horas"),
                            rs.getString("objetivo_general"),
                            rs.getString("metodologia"),
                            rs.getString("resultado_obtenido"),
                            rs.getInt("idProyecto"),
                            rs.getString("matricula"),
                            rs.getString("observaciones"),
                            String.valueOf(rs.getInt("idEvidencia"))
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<ReportDTO> getAllReports() throws SQLException {
        List<ReportDTO> reports = new ArrayList<>();
        try (ConecctionDataBase db = new ConecctionDataBase();
             Connection conn = db.connectDB();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                reports.add(new ReportDTO(
                        String.valueOf(rs.getInt("numReporte")),
                        rs.getDate("fecha_reporte"),
                        rs.getInt("total_horas"),
                        rs.getString("objetivo_general"),
                        rs.getString("metodologia"),
                        rs.getString("resultado_obtenido"),
                        rs.getInt("idProyecto"),
                        rs.getString("matricula"),
                        rs.getString("observaciones"),
                        String.valueOf(rs.getInt("idEvidencia"))
                ));
            }
        }
        return reports;
    }
}