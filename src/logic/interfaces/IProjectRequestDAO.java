package logic.interfaces;

import logic.DTO.ProjectRequestDTO;

import java.sql.SQLException;
import java.util.List;

public interface IProjectRequestDAO {
    boolean insertProjectRequest(ProjectRequestDTO request) throws SQLException;

    boolean updateProjectRequest(ProjectRequestDTO request) throws SQLException;

    boolean deleteProjectRequest(int requestId) throws SQLException;

    ProjectRequestDTO searchProjectRequestById(int requestId) throws SQLException;

    List<ProjectRequestDTO> getAllProjectRequests() throws SQLException;

    List<ProjectRequestDTO> searchProjectRequestByTuiton(String tuiton) throws SQLException;
}