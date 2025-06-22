package logic.services;

import logic.DAO.ProjectDAO;
import logic.DTO.ProjectDTO;
import logic.exceptions.RepeatedId;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProjectService {
    private final ProjectDAO projectDAO;

    public ProjectService(Connection connection) {
        this.projectDAO = new ProjectDAO();
    }

    public boolean registerProject(ProjectDTO project) throws SQLException, IOException, RepeatedId {

        boolean success = projectDAO.insertProject(project);
        if (!success) {
            throw new SQLException("No se pudo registrar el proyecto.");
        }
        return success;
    }

    public boolean updateProject(ProjectDTO project) throws SQLException, IOException, RepeatedId {
        boolean success = projectDAO.updateProject(project);
        if (!success) {
            throw new SQLException("No se pudo actualizar el proyecto.");
        }
        return success;
    }

    public List<ProjectDTO> getAllProjects() throws SQLException, IOException {
        return projectDAO.getAllProjects();
    }

    public ProjectDTO searchProjectById(String id) throws SQLException, IOException {
        return projectDAO.searchProjectById(id);
    }

    public ProjectDTO searchProjectByName(String name) throws SQLException, IOException {
        return projectDAO.searchProjectByName(name);
    }

}
