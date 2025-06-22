package logic.interfaces;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.ReportDTO;

public interface IReportDAO {
    boolean insertReport(ReportDTO report) throws SQLException, IOException;

    boolean updateReport(ReportDTO report) throws SQLException, IOException;

    boolean deleteReport(String numberReport) throws SQLException, IOException;

    ReportDTO searchReportById(String numberReport) throws SQLException, IOException;

    List<ReportDTO> getAllReports() throws SQLException, IOException;

    int getTotalReportedHoursByStudent(String tuition) throws SQLException, IOException;
}
