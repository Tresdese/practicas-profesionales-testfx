package logic.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.ProjectDTO;

public interface IProjectDAO {
    boolean insertProject(ProjectDTO project) throws SQLException;

    boolean updateProject(ProjectDTO project) throws SQLException;

    boolean deleteProject(String idProject) throws SQLException;

    ProjectDTO searchProjectById(String idProject) throws SQLException;

    List<ProjectDTO> getAllProjects() throws SQLException;
}
