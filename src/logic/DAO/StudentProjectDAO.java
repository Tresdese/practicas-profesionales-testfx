package logic.DAO;

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
    private final static String SQL_UPDATE = "UPDATE eproyecto_estudiante SET matricula = ? WHERE idProyecto = ?";
    private final static String SQL_DELETE = "DELETE FROM proyecto_estudiante WHERE idProyecto = ?";
    private final static String SQL_SELECT = "SELECT * FROM proyecto_estudiante WHERE idProyecto = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM proyecto_estudiante";

    public boolean insertStudentProject(StudentProjectDTO studentProject, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
            ps.setString(1, studentProject.getIdProject());
            ps.setString(2, studentProject.getTuiton());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStudentProject(StudentProjectDTO studentProject, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, studentProject.getTuiton());
            ps.setString(2, studentProject.getIdProject());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteStudentProject(StudentProjectDTO studentProject, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setString(1, studentProject.getIdProject());
            return ps.executeUpdate() > 0;
        }
    }

    public StudentProjectDTO getStudentProject(String idProject, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ps.setString(1, idProject);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new StudentProjectDTO(rs.getString("idProyecto"), rs.getString("matricula"));
                }
            }
        }
        return null;
    }

    public List<StudentProjectDTO> getAllStudentProjects(Connection connection) throws SQLException {
        List<StudentProjectDTO> studentProjects = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                studentProjects.add(new StudentProjectDTO(rs.getString("idProyecto"), rs.getString("matricula")));
            }
        }
        return studentProjects;
    }
}
