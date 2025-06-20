package logic.DAO;

import data_access.ConnectionDataBase;
import logic.DTO.ProjectPresentationDTO;
import logic.DTO.Tipe;
import logic.interfaces.IProjectPresentationDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectPresentationDAO implements IProjectPresentationDAO {

    private static final String SQL_INSERT = "INSERT INTO presentacion_proyecto (idPresentacion, idProyecto, fecha, tipo) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE presentacion_proyecto SET idProyecto = ?, fecha = ?, tipo = ? WHERE idPresentacion = ?";
    private static final String SQL_DELETE = "DELETE FROM presentacion_proyecto WHERE idPresentacion = ?";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM presentacion_proyecto WHERE idPresentacion = ?";
    private static final String SQL_SELECT_BY_PROJECT_ID = "SELECT * FROM presentacion_proyecto WHERE idProyecto = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM presentacion_proyecto";
    private static final String SQL_SELECT_UPCOMING = "SELECT * FROM presentacion_proyecto WHERE fecha > NOW() ORDER BY fecha ASC";

    public boolean insertProjectPresentation(ProjectPresentationDTO projectPresentation) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setInt(1, projectPresentation.getIdPresentation());
            statement.setString(2, projectPresentation.getIdProject());
            statement.setTimestamp(3, projectPresentation.getDate());
            statement.setString(4, projectPresentation.getTipe().name());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateProjectPresentation(ProjectPresentationDTO projectPresentation) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, projectPresentation.getIdProject());
            statement.setTimestamp(2, projectPresentation.getDate());
            statement.setString(3, projectPresentation.getTipe().name());
            statement.setInt(4, projectPresentation.getIdPresentation());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteProjectPresentation(int idPresentation) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setInt(1, idPresentation);
            return statement.executeUpdate() > 0;
        }
    }

    public ProjectPresentationDTO searchProjectPresentationById(int idPresentation) throws SQLException {
        ProjectPresentationDTO projectPresentation = null;
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            statement.setInt(1, idPresentation);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    projectPresentation = new ProjectPresentationDTO(
                            resultSet.getInt("idPresentacion"),
                            resultSet.getString("idProyecto"),
                            resultSet.getTimestamp("fecha"),
                            Tipe.valueOf(resultSet.getString("tipo"))
                    );
                }
            }
        }
        return projectPresentation;
    }

    public List<ProjectPresentationDTO> searchProjectPresentationsByProjectId(String idProject) throws SQLException {
        List<ProjectPresentationDTO> presentations = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_PROJECT_ID)) {
            statement.setString(1, idProject);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    presentations.add(new ProjectPresentationDTO(
                            resultSet.getInt("idPresentacion"),
                            resultSet.getString("idProyecto"),
                            resultSet.getTimestamp("fecha"),
                            Tipe.valueOf(resultSet.getString("tipo"))
                    ));
                }
            }
        }
        return presentations;
    }

    public List<ProjectPresentationDTO> getAllProjectPresentations() throws SQLException {
        List<ProjectPresentationDTO> presentations = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                presentations.add(new ProjectPresentationDTO(
                        resultSet.getInt("idPresentacion"),
                        resultSet.getString("idProyecto"),
                        resultSet.getTimestamp("fecha"),
                        Tipe.valueOf(resultSet.getString("tipo"))
                ));
            }
        }
        return presentations;
    }

    public List<ProjectPresentationDTO> getUpcomingPresentations() throws SQLException {
        List<ProjectPresentationDTO> upcomingPresentations = new ArrayList<>();

        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_UPCOMING);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                upcomingPresentations.add(new ProjectPresentationDTO(
                        resultSet.getInt("idPresentacion"),
                        resultSet.getString("idProyecto"),
                        resultSet.getTimestamp("fecha"),
                        Tipe.valueOf(resultSet.getString("tipo"))
                ));
            }
        }
        return upcomingPresentations;
    }
}