package logic.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.ReportDTO;

public interface IReportDAO {
    boolean insertReport(ReportDTO report, Connection connection) throws SQLException;

    boolean updateReport(ReportDTO report, Connection connection) throws SQLException;

    boolean deleteReport(String numberReport, Connection connection) throws SQLException;

    ReportDTO getReport(String numberReport, Connection connection) throws SQLException;

    List<ReportDTO> getAllReports(Connection connection) throws SQLException;
}
