package logic.interfaces;

import logic.DTO.ActivityReportDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IActivityReportDAO {
    boolean insertActivityReport(ActivityReportDTO activityReport, Connection connection) throws SQLException;

    boolean updateActivityReport(ActivityReportDTO activityReport, Connection connection) throws SQLException;

    boolean deleteActivityReport(String reportNumber, Connection connection) throws SQLException;

    ActivityReportDTO getActivityReport(String reportNumber, Connection connection) throws SQLException;

    List<ActivityReportDTO> getAllActivityReports(Connection connection) throws SQLException;
}