package logic.interfaces;

import logic.DTO.ProjectRequestDTO;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface IProjectRequestDAO {
    boolean insertProjectRequest(ProjectRequestDTO request) throws SQLException, IOException;

    boolean updateProjectRequest(ProjectRequestDTO request) throws SQLException, IOException;

    boolean updateProjectRequestStatus(int requestId, String status) throws SQLException, IOException;

    boolean deleteProjectRequest(int requestId) throws SQLException, IOException;

    ProjectRequestDTO searchProjectRequestById(int requestId) throws SQLException, IOException;

    List<ProjectRequestDTO> getAllProjectRequests() throws SQLException, IOException;

    List<ProjectRequestDTO> getProjectRequestsByTuiton(String tuiton) throws SQLException, IOException;

    ProjectRequestDTO mapResultSetToDTO(ResultSet rs) throws SQLException;

    List<ProjectRequestDTO> getProjectRequestsByStatus(String status) throws SQLException, IOException;
}