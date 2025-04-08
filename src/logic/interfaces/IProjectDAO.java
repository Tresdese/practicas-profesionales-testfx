package logic.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.ProjectDTO;

public interface IProjectDAO {
    boolean insertProject(ProjectDTO project, Connection connection) throws SQLException;

    boolean updateProject(ProjectDTO project, Connection connection) throws SQLException;

    boolean deleteProject(String idProject, Connection connection) throws SQLException;

    ProjectDTO getProject(String idProject, Connection connection) throws SQLException;

    List<ProjectDTO> getAllProjects(Connection connection) throws SQLException;
}
