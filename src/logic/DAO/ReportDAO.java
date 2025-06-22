package logic.DAO;

import data_access.ConnectionDataBase;
import logic.DTO.ReportDTO;
import logic.interfaces.IReportDAO;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO implements IReportDAO {

    private static final String SQL_INSERT = "INSERT INTO reporte (fecha_reporte, total_horas, objetivo_general, metodologia, resultado_obtenido, idProyecto, matricula, observaciones, idEvidencia) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE reporte SET fecha_reporte = ?, total_horas = ?, objetivo_general = ?, metodologia = ?, resultado_obtenido = ?, idProyecto = ?, matricula = ?, observaciones = ?, idEvidencia = ? WHERE numReporte = ?";
    private static final String SQL_DELETE = "DELETE FROM reporte WHERE numReporte = ?";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM reporte WHERE numReporte = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM reporte";
    private static final String SQL_TOTAL_HOURS_STUDENT = "SELECT SUM(total_horas) FROM reporte WHERE matricula = ?";

    @Override
    public boolean insertReport(ReportDTO report) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setDate(1, new java.sql.Date(report.getReportDate().getTime()));
            statement.setInt(2, report.getTotalHours());
            statement.setString(3, report.getGeneralObjective());
            statement.setString(4, report.getMethodology());
            statement.setString(5, report.getObtainedResult());
            statement.setInt(6, report.getProjectId());
            statement.setString(7, report.getTuition());
            statement.setString(8, report.getObservations());
            statement.setInt(9, Integer.parseInt(report.getIdEvidence()));
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
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
    public boolean updateReport(ReportDTO report) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setDate(1, new java.sql.Date(report.getReportDate().getTime()));
            statement.setInt(2, report.getTotalHours());
            statement.setString(3, report.getGeneralObjective());
            statement.setString(4, report.getMethodology());
            statement.setString(5, report.getObtainedResult());
            statement.setInt(6, report.getProjectId());
            statement.setString(7, report.getTuition());
            statement.setString(8, report.getObservations());
            statement.setInt(9, Integer.parseInt(report.getIdEvidence()));
            statement.setInt(10, Integer.parseInt(report.getNumberReport()));
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteReport(String numberReport) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setInt(1, Integer.parseInt(numberReport));
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public ReportDTO searchReportById(String numberReport) throws SQLException, IOException {
        ReportDTO reportDTO = new ReportDTO("N/A", null, 0, "N/A", "N/A", "N/A", 0, "N/A", "N/A", "0");
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            statement.setInt(1, Integer.parseInt(numberReport));
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    reportDTO = new ReportDTO(
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
        return reportDTO;
    }

    @Override
    public List<ReportDTO> getAllReports() throws SQLException, IOException {
        List<ReportDTO> reports = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = statement.executeQuery()) {
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

    public int getTotalReportedHoursByStudent(String tuition) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_TOTAL_HOURS_STUDENT)) {
            statement.setString(1, tuition);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}