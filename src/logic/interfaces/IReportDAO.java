package logic.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.ReportDTO;

public interface IReportDAO {
    boolean insertReport(ReportDTO report) throws SQLException;

    boolean updateReport(ReportDTO report) throws SQLException;

    boolean deleteReport(String numberReport) throws SQLException;

    ReportDTO searchReportById(String numberReport) throws SQLException;

    List<ReportDTO> getAllReports() throws SQLException;
}
