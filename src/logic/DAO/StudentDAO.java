package logic.DAO;

import logic.DTO.StudentDTO;
import logic.exceptions.RepeatedTuiton;
import logic.interfaces.IStudentDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO implements IStudentDAO {
    private final static String SQL_INSERT = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE estudiante SET estado = ?, nombres = ?, apellidos = ?, telefono = ?, correo = ?, usuario = ?, contraseña = ?, NRC = ?, avanceCrediticio = ? WHERE matricula = ?";
    private final static String SQL_UPDATE_STATE = "UPDATE estudiante SET estado = ? WHERE matricula = ?";
    private final static String SQL_DELETE = "DELETE FROM estudiante WHERE matricula = ?";
    private final static String SQL_SELECT = "SELECT * FROM estudiante WHERE matricula = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM estudiante";

    public boolean insertStudent(StudentDTO student, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, student.getTuiton());
            statement.setInt(2, student.getState());
            statement.setString(3, student.getNames());
            statement.setString(4, student.getSurnames());
            statement.setString(5, student.getPhone());
            statement.setString(6, student.getEmail());
            statement.setString(7, student.getUser());
            statement.setString(8, student.getPassword());
            statement.setString(9, student.getNRC());
            statement.setString(10, student.getCreditAdvance());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateStudent(StudentDTO student, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setInt(1, student.getState());
            statement.setString(2, student.getNames());
            statement.setString(3, student.getSurnames());
            statement.setString(4, student.getPhone());
            statement.setString(5, student.getEmail());
            statement.setString(6, student.getUser());
            statement.setString(7, student.getPassword());
            statement.setString(8, student.getNRC());
            statement.setString(9, student.getCreditAdvance());
            statement.setString(10, student.getTuiton());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateStudentState(String tuiton, int state, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_STATE)) {
            statement.setInt(1, state);
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

    public StudentDTO searchStudentByTuiton(String tuiton, Connection connection) throws SQLException {
        StudentDTO student = new StudentDTO("N/A", -1, "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A");
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, tuiton);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    student = new StudentDTO(
                            resultSet.getString("matricula"),
                            resultSet.getInt("estado"),
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
        return student;
    }

    public List<StudentDTO> getAllStudents(Connection connection) throws SQLException {
        List<StudentDTO> students = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                students.add(new StudentDTO(
                        resultSet.getString("matricula"),
                        resultSet.getInt("estado"),
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

    public boolean isTuitonRegistered(String tuiton, Connection connection) throws SQLException  {
        String query = "SELECT COUNT(*) FROM estudiante WHERE matricula = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, tuiton);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean isPhoneRegistered(String phone, Connection connection) throws SQLException {
        String query = "SELECT COUNT(*) FROM estudiante WHERE telefono = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, phone);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean isEmailRegistered(String email, Connection connection) throws SQLException {
        String query = "SELECT COUNT(*) FROM estudiante WHERE correo = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}