package logic.DAO;

import data_access.ConnectionDataBase;
import logic.DTO.ProjectDTO;
import logic.interfaces.IProjectDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO implements IProjectDAO {

    private static final String SQL_INSERT = "INSERT INTO proyecto (idProyecto, nombre, descripcion, fechaAproximada, fechaInicio, idUsuario, idOrganizacion) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE proyecto SET nombre = ?, descripcion = ?, fechaAproximada = ?, fechaInicio = ?, idUsuario = ?, idOrganizacion = ? WHERE idProyecto = ?";
    private static final String SQL_DELETE = "DELETE FROM proyecto WHERE idProyecto = ?";
    private static final String SQL_SELECT_PROJECT_NAME_BY_ID = "SELECT nombre FROM proyecto WHERE idProyecto = ?";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM proyecto WHERE idProyecto = ?";
    private static final String SQL_SELECT_BY_NAME = "SELECT * FROM proyecto WHERE nombre = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM proyecto";

    public boolean insertProject(ProjectDTO project) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, project.getIdProject());
            statement.setString(2, project.getName());
            statement.setString(3, project.getDescription());
            statement.setTimestamp(4, project.getApproximateDate());
            statement.setTimestamp(5, project.getStartDate());
            statement.setString(6, project.getIdUser());
            statement.setInt(7, project.getIdOrganization());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateProject(ProjectDTO project) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, project.getName());
            statement.setString(2, project.getDescription());
            statement.setTimestamp(3, project.getApproximateDate());
            statement.setTimestamp(4, project.getStartDate());
            statement.setString(5, project.getIdUser());
            statement.setInt(6, project.getIdOrganization());
            statement.setString(7, project.getIdProject());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteProject(String idProject) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, idProject);
            return statement.executeUpdate() > 0;
        }
    }

    public ProjectDTO searchProjectById(String idProject) throws SQLException {
        ProjectDTO project = new ProjectDTO("-1", "N/A", "N/A", null, null, "N/A", 0);
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            statement.setString(1, idProject);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    project = new ProjectDTO(
                            resultSet.getString("idProyecto"),
                            resultSet.getString("nombre"),
                            resultSet.getString("descripcion"),
                            resultSet.getTimestamp("fechaAproximada"),
                            resultSet.getTimestamp("fechaInicio"),
                            resultSet.getString("idUsuario"),
                            resultSet.getInt("idOrganizacion")
                    );
                }
            }
        }
        return project;
    }

    public ProjectDTO searchProjectByName(String name) throws SQLException {
        ProjectDTO project = new ProjectDTO("-1", "N/A", "N/A", null, null, "N/A", 0);
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_NAME)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    project = new ProjectDTO(
                            resultSet.getString("idProyecto"),
                            resultSet.getString("nombre"),
                            resultSet.getString("descripcion"),
                            resultSet.getTimestamp("fechaAproximada"),
                            resultSet.getTimestamp("fechaInicio"),
                            resultSet.getString("idUsuario"),
                            resultSet.getInt("idOrganizacion")
                    );
                }
            }
        }
        return project;
    }

    public String getProyectNameById(String idProject) throws SQLException {
        String projectName = "";
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_PROJECT_NAME_BY_ID)) {
            statement.setString(1, idProject);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    projectName = resultSet.getString("nombre");
                }
            }
        }
        return projectName;
    }

    public List<ProjectDTO> getAllProjects() throws SQLException {
        List<ProjectDTO> projects = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                projects.add(new ProjectDTO(
                        resultSet.getString("idProyecto"),
                        resultSet.getString("nombre"),
                        resultSet.getString("descripcion"),
                        resultSet.getTimestamp("fechaAproximada"),
                        resultSet.getTimestamp("fechaInicio"),
                        resultSet.getString("idUsuario"),
                        resultSet.getInt("idOrganizacion")
                ));
            }
        }
        return projects;
    }
}