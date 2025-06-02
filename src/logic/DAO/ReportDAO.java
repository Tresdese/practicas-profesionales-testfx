package logic.DAO;

import data_access.ConecctionDataBase;
import logic.DTO.ReportDTO;
import logic.interfaces.IReportDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO implements IReportDAO {

    private static final String SQL_INSERT = "INSERT INTO reporte (fecha_reporte, total_horas, objetivo_general, metodologia, resultado_obtenido, idProyecto, matricula, observaciones, idEvidencia) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE reporte SET fecha = ?, horasTotales = ?, objetivoGeneral = ?, metodologia = ?, resultadoObtenido = ?, idProyecto = ?, matricula = ?, observaciones = ?, idEvidencia = ? WHERE numReporte = ?";
    private static final String SQL_DELETE = "DELETE FROM reporte WHERE numReporte = ?";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM reporte WHERE numReporte = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM reporte";

    @Override
    public boolean insertReport(ReportDTO report) throws SQLException {
        try (ConecctionDataBase db = new ConecctionDataBase();
             Connection conn = db.connectDB();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, new Timestamp(report.getReportDate().getTime()));
            stmt.setInt(2, report.getTotalHours());
            stmt.setString(3, report.getGeneralObjective());
            stmt.setString(4, report.getMethodology());
            stmt.setString(5, report.getObtainedResult());
            stmt.setInt(6, report.getProjectId());
            stmt.setString(7, report.getTuition());
            stmt.setString(8, report.getObservations());
            stmt.setString(9, report.getIdEvidence());
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
            stmt.setTimestamp(1, new Timestamp(report.getReportDate().getTime()));
            stmt.setInt(2, report.getTotalHours());
            stmt.setString(3, report.getGeneralObjective());
            stmt.setString(4, report.getMethodology());
            stmt.setString(5, report.getObtainedResult());
            stmt.setInt(6, report.getProjectId());
            stmt.setString(7, report.getTuition());
            stmt.setString(8, report.getObservations());
            stmt.setString(9, report.getIdEvidence());
            stmt.setString(10, report.getNumberReport());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteReport(String numberReport) throws SQLException {
        try (ConecctionDataBase db = new ConecctionDataBase();
             Connection conn = db.connectDB();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            stmt.setString(1, numberReport);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public ReportDTO searchReportById(String numberReport) throws SQLException {
        try (ConecctionDataBase db = new ConecctionDataBase();
             Connection conn = db.connectDB();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            stmt.setString(1, numberReport);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ReportDTO(
                            rs.getString("numReporte"),
                            rs.getTimestamp("fecha"),
                            rs.getInt("horasTotales"),
                            rs.getString("objetivoGeneral"),
                            rs.getString("metodologia"),
                            rs.getString("resultadoObtenido"),
                            rs.getInt("idProyecto"),
                            rs.getString("matricula"),
                            rs.getString("observaciones"),
                            rs.getString("idEvidencia")
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
                        rs.getString("numReporte"),
                        rs.getTimestamp("fecha"),
                        rs.getInt("horasTotales"),
                        rs.getString("objetivoGeneral"),
                        rs.getString("metodologia"),
                        rs.getString("resultadoObtenido"),
                        rs.getInt("idProyecto"),
                        rs.getString("matricula"),
                        rs.getString("observaciones"),
                        rs.getString("idEvidencia")
                ));
            }
        }
        return reports;
    }
}