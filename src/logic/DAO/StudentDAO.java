package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.StudentDTO;
import logic.interfaces.IStudentDAO;

public class StudentDAO implements IStudentDAO {
    private final static String SQL_INSERT = "INSERT INTO estudiante (matricula, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE estudiante SET nombres = ?, apellidos = ?, telefono = ?, correo = ?, usuario = ?, contraseña = ?, NRC = ?, avanceCrediticio = ? WHERE matricula = ?";
    private final static String SQL_DELETE = "DELETE FROM estudiante WHERE matricula = ?";
    private final static String SQL_SELECT = "SELECT * FROM estudiante WHERE matricula = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM estudiante";

    public boolean insertStudent(StudentDTO student, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
            ps.setString(1, student.getTuiton());
            ps.setString(2, student.getNames());
            ps.setString(3, student.getSurnames());
            ps.setString(4, student.getPhone());
            ps.setString(5, student.getEmail());
            ps.setString(6, student.getUser());
            ps.setString(7, student.getPassword());
            ps.setString(8, student.getNRC());
            ps.setString(9, student.getCreditAdvance());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStudent(StudentDTO student, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, student.getNames());
            ps.setString(2, student.getSurnames());
            ps.setString(3, student.getPhone());
            ps.setString(4, student.getEmail());
            ps.setString(5, student.getUser());
            ps.setString(6, student.getPassword());
            ps.setString(7, student.getNRC());
            ps.setString(8, student.getCreditAdvance());
            ps.setString(9, student.getTuiton());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteStudent(StudentDTO student, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setString(1, student.getTuiton());
            return ps.executeUpdate() > 0;
        }
    }

    public StudentDTO getStudent(String tuiton, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ps.setString(1, tuiton);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new StudentDTO(
                        rs.getString("matricula"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("telefono"),
                        rs.getString("correo"),
                        rs.getString("usuario"),
                        rs.getString("contraseña"),
                        rs.getString("NRC"),
                        rs.getString("avanceCrediticio")
                    );
                }
            }
        }
        return null;
    }

    public List<StudentDTO> getAllStudents(Connection connection) throws SQLException {
        List<StudentDTO> students = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(new StudentDTO(
                    rs.getString("matricula"),
                    rs.getString("nombres"),
                    rs.getString("apellidos"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getString("usuario"),
                    rs.getString("contraseña"),
                    rs.getString("NRC"),
                    rs.getString("avanceCrediticio")
                ));
            }
        }
        return students;
    }
}
