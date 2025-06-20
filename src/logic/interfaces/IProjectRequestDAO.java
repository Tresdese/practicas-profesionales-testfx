package logic.interfaces;

import logic.DTO.ProjectRequestDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface IProjectRequestDAO {
    boolean insertProjectRequest(ProjectRequestDTO request) throws SQLException;

    boolean updateProjectRequest(ProjectRequestDTO request) throws SQLException;

    boolean updateProjectRequestStatus(int requestId, String status) throws SQLException;

    boolean deleteProjectRequest(int requestId) throws SQLException;

    ProjectRequestDTO searchProjectRequestById(int requestId) throws SQLException;

    List<ProjectRequestDTO> getAllProjectRequests() throws SQLException;

    List<ProjectRequestDTO> getProjectRequestsByTuiton(String tuiton) throws SQLException;

    ProjectRequestDTO mapResultSetToDTO(ResultSet rs) throws SQLException;

    List<ProjectRequestDTO> getProjectRequestsByStatus(String status) throws SQLException;
}