package logic.interfaces;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.ProjectDTO;

public interface IProjectDAO {
    boolean insertProject(ProjectDTO project) throws SQLException, IOException;

    boolean updateProject(ProjectDTO project) throws SQLException, IOException;

    boolean deleteProject(String idProject) throws SQLException, IOException;

    ProjectDTO searchProjectById(String idProject) throws SQLException, IOException;

    ProjectDTO searchProjectByName(String name) throws SQLException, IOException;

    String getProyectNameById(int idProject) throws SQLException, IOException;

    List<ProjectDTO> getAllProjects() throws SQLException, IOException;
}
