package logic.interfaces;

import logic.DTO.ActivityReportDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IActivityReportDAO {

    boolean insertActivityReport(ActivityReportDTO activityReport) throws SQLException;

    boolean updateActivityReport(ActivityReportDTO activityReport) throws SQLException;

    boolean deleteActivityReport(String reportNumber) throws SQLException;

    ActivityReportDTO searchActivityReportByReportNumber(String reportNumber) throws SQLException;

    List<ActivityReportDTO> getAllActivityReports() throws SQLException;
}