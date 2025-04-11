package data_access.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.StudentProjectDTO;
import logic.interfaces.IStudentProjectDAO;

public class StudentProjectDAO implements IStudentProjectDAO {
    private final static String SQL_INSERT = "INSERT INTO proyecto_estudiante (idProyecto, matricula) VALUES (?, ?)";
    private final static String SQL_UPDATE = "UPDATE proyecto_estudiante SET matricula = ? WHERE idProyecto = ?";
    private final static String SQL_DELETE = "DELETE FROM proyecto_estudiante WHERE idProyecto = ?";
    private final static String SQL_SELECT = "SELECT * FROM proyecto_estudiante WHERE idProyecto = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM proyecto_estudiante";

    public boolean insertStudentProject(StudentProjectDTO studentProject, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, studentProject.getIdProject());
            statement.setString(2, studentProject.getTuiton());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateStudentProject(StudentProjectDTO studentProject, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, studentProject.getTuiton());
            statement.setString(2, studentProject.getIdProject());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteStudentProject(StudentProjectDTO studentProject, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, studentProject.getIdProject());
            return statement.executeUpdate() > 0;
        }
    }

    public StudentProjectDTO getStudentProject(String idProject, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, idProject);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new StudentProjectDTO(resultSet.getString("idProyecto"), resultSet.getString("matricula"));
                }
            }
        }
        return null;
    }

    public List<StudentProjectDTO> getAllStudentProjects(Connection connection) throws SQLException {
        List<StudentProjectDTO> studentProjects = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                studentProjects.add(new StudentProjectDTO(resultSet.getString("idProyecto"), resultSet.getString("matricula")));
            }
        }
        return studentProjects;
    }
}
