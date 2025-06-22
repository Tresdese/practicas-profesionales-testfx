package logic.interfaces;

import logic.DTO.ActivityReportDTO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IActivityReportDAO {

    boolean insertActivityReport(ActivityReportDTO activityReport) throws SQLException, IOException;

    boolean updateActivityReport(ActivityReportDTO activityReport) throws SQLException, IOException;

    boolean deleteActivityReport(String reportNumber) throws SQLException, IOException;

    ActivityReportDTO searchActivityReportByReportNumber(String reportNumber) throws SQLException, IOException;

    List<ActivityReportDTO> getAllActivityReports() throws SQLException, IOException;
}