package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.ProjectDTO;
import logic.interfaces.IProjectDAO;

public class ProjectDAO implements IProjectDAO {
    private final static String SQL_INSERT = "INSERT INTO proyecto (idProyecto, nombre, descripcion, fechaAproximada, fechaInicio, idUsuario) VALUES (?, ?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE proyecto SET nombre = ?, descripcion = ?, fechaAproximada = ?, fechaInicio = ?, idUsuario = ? WHERE idProyecto = ?";
    private final static String SQL_DELETE = "DELETE FROM proyecto WHERE idProyecto = ?";
    private final static String SQL_SELECT = "SELECT * FROM proyecto WHERE idProyecto = ?";

    public boolean insertProject(ProjectDTO project, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
            ps.setString(1, project.getIdProject());
            ps.setString(2, project.getName());
            ps.setString(3, project.getDescription());
            ps.setTimestamp(4, project.getApproximateDate());
            ps.setTimestamp(5, project.getStartDate());
            ps.setString(6, project.getIdUser());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateProject(ProjectDTO project, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.setTimestamp(3, project.getApproximateDate());
            ps.setTimestamp(4, project.getStartDate());
            ps.setString(5, project.getIdUser());
            ps.setString(6, project.getIdProject());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteProject(String idProject, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setString(1, idProject);
            return ps.executeUpdate() > 0;
        }
    }

    public ProjectDTO getProject(String idProject, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ps.setString(1, idProject);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ProjectDTO(
                        rs.getString("idProyecto"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getTimestamp("fechaAproximada"),
                        rs.getTimestamp("fechaInicio"),
                        rs.getString("idUsuario")
                    );
                }
            }
        }
        return null;
    }

    public List<ProjectDTO> getAllProjects(Connection connection) throws SQLException {
        List<ProjectDTO> projects = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT * FROM proyecto";
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                projects.add(new ProjectDTO(
                    rs.getString("idProyecto"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getTimestamp("fechaAproximada"),
                    rs.getTimestamp("fechaInicio"),
                    rs.getString("idUsuario")
                ));
            }
        }
        return projects;
    }
}
