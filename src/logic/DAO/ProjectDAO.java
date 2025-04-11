package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.ProjectDTO;

public class ProjectDAO {
    private final static String SQL_INSERT = "INSERT INTO proyecto (idProyecto, nombre, descripcion, fechaAproximada, fechaInicio, idUsuario) VALUES (?, ?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE proyecto SET nombre = ?, descripcion = ?, fechaAproximada = ?, fechaInicio = ?, idUsuario = ? WHERE idProyecto = ?";
    private final static String SQL_DELETE = "DELETE FROM proyecto WHERE idProyecto = ?";
    private final static String SQL_SELECT = "SELECT * FROM proyecto WHERE idProyecto = ?";

    public boolean insertProject(ProjectDTO project, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, project.getIdProject());
            statement.setString(2, project.getName());
            statement.setString(3, project.getDescription());
            statement.setTimestamp(4, project.getApproximateDate());
            statement.setTimestamp(5, project.getStartDate());
            statement.setString(6, project.getIdUser());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateProject(ProjectDTO project, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, project.getName());
            statement.setString(2, project.getDescription());
            statement.setTimestamp(3, project.getApproximateDate());
            statement.setTimestamp(4, project.getStartDate());
            statement.setString(5, project.getIdUser());
            statement.setString(6, project.getIdProject());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteProject(String idProject, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, idProject);
            return statement.executeUpdate() > 0;
        }
    }

    public ProjectDTO getProject(String idProject, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, idProject);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new ProjectDTO(
                        resultSet.getString("idProyecto"),
                        resultSet.getString("nombre"),
                        resultSet.getString("descripcion"),
                        resultSet.getTimestamp("fechaAproximada"),
                        resultSet.getTimestamp("fechaInicio"),
                        resultSet.getString("idUsuario")
                    );
                }
            }
        }
        return null;
    }

    public List<ProjectDTO> getAllProjects(Connection connection) throws SQLException {
        List<ProjectDTO> projects = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT * FROM proyecto";
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                projects.add(new ProjectDTO(
                    resultSet.getString("idProyecto"),
                    resultSet.getString("nombre"),
                    resultSet.getString("descripcion"),
                    resultSet.getTimestamp("fechaAproximada"),
                    resultSet.getTimestamp("fechaInicio"),
                    resultSet.getString("idUsuario")
                ));
            }
        }
        return projects;
    }
}
