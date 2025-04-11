package data_access.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.StudentDTO;

public class StudentDAO {
    private final static String SQL_INSERT = "INSERT INTO estudiante (matricula, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE estudiante SET nombres = ?, apellidos = ?, telefono = ?, correo = ?, usuario = ?, contraseña = ?, NRC = ?, avanceCrediticio = ? WHERE matricula = ?";
    private final static String SQL_UPDATE_STATE = "UPDATE estudiante SET estado = ? WHERE matricula = ?";
    private final static String SQL_DELETE = "DELETE FROM estudiante WHERE matricula = ?";
    private final static String SQL_SELECT = "SELECT * FROM estudiante WHERE matricula = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM estudiante";

    public boolean insertStudent(StudentDTO student, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, student.getTuiton());
            statement.setString(2, student.getNames());
            statement.setString(3, student.getSurnames());
            statement.setString(4, student.getPhone());
            statement.setString(5, student.getEmail());
            statement.setString(6, student.getUser());
            statement.setString(7, student.getPassword());
            statement.setString(8, student.getNRC());
            statement.setString(9, student.getCreditAdvance());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateStudent(StudentDTO student, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, student.getNames());
            statement.setString(2, student.getSurnames());
            statement.setString(3, student.getPhone());
            statement.setString(4, student.getEmail());
            statement.setString(5, student.getUser());
            statement.setString(6, student.getPassword());
            statement.setString(7, student.getNRC());
            statement.setString(8, student.getCreditAdvance());
            statement.setString(9, student.getTuiton());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateStudentState(String tuiton, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_STATE)) {
            statement.setInt(1, 1);
            statement.setString(2, tuiton);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteStudent(String tuiton, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, tuiton);
            return statement.executeUpdate() > 0;
        }
    }

    public StudentDTO getStudent(String tuiton, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, tuiton);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new StudentDTO(
                        resultSet.getString("matricula"),
                        resultSet.getString("nombres"),
                        resultSet.getString("apellidos"),
                        resultSet.getString("telefono"),
                        resultSet.getString("correo"),
                        resultSet.getString("usuario"),
                        resultSet.getString("contraseña"),
                        resultSet.getString("NRC"),
                        resultSet.getString("avanceCrediticio")
                    );
                }
            }
        }
        return null;
    }

    public List<StudentDTO> getAllStudents(Connection connection) throws SQLException {
        List<StudentDTO> students = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                students.add(new StudentDTO(
                    resultSet.getString("matricula"),
                    resultSet.getString("nombres"),
                    resultSet.getString("apellidos"),
                    resultSet.getString("telefono"),
                    resultSet.getString("correo"),
                    resultSet.getString("usuario"),
                    resultSet.getString("contraseña"),
                    resultSet.getString("NRC"),
                    resultSet.getString("avanceCrediticio")
                ));
            }
        }
        return students;
    }
}
