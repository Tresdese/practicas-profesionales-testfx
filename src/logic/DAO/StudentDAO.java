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
    private final Connection connection;

    private static final String SQL_INSERT = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE estudiante SET estado = ?, nombres = ?, apellidos = ?, telefono = ?, correo = ?, usuario = ?, contraseña = ?, NRC = ?, avanceCrediticio = ? WHERE matricula = ?";
    private static final String SQL_UPDATE_STATE = "UPDATE estudiante SET estado = ? WHERE matricula = ?";
    private static final String SQL_DELETE = "DELETE FROM estudiante WHERE matricula = ?";
    private static final String SQL_SELECT = "SELECT * FROM estudiante WHERE matricula = ?";
    private static final String SQL_SELECT_BY_USER_AND_PASSWORD = "SELECT * FROM estudiante WHERE usuario = ? AND contraseña = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM estudiante";
    private static final String SQL_COUNT_BY_TUITON = "SELECT COUNT(*) FROM estudiante WHERE matricula = ?";
    private static final String SQL_COUNT_BY_PHONE = "SELECT COUNT(*) FROM estudiante WHERE telefono = ?";
    private static final String SQL_COUNT_BY_EMAIL = "SELECT COUNT(*) FROM estudiante WHERE correo = ?";

    public StudentDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean insertStudent(StudentDTO student) throws SQLException {
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

    public boolean updateStudent(StudentDTO student) throws SQLException {
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

    public boolean updateStudentState(String tuiton, int state) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_STATE)) {
            statement.setInt(1, state);
            statement.setString(2, tuiton);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteStudent(String tuiton) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, tuiton);
            return statement.executeUpdate() > 0;
        }
    }

    public StudentDTO searchStudentByTuiton(String tuiton) throws SQLException {
        StudentDTO student = null;
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

    public List<StudentDTO> getAllStudents() throws SQLException {
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

    public StudentDTO searchStudentByUserAndPassword(String username, String password) throws SQLException {
        StudentDTO student = null;
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_USER_AND_PASSWORD)) {
            statement.setString(1, username);
            statement.setString(2, password);
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

    public boolean isTuitonRegistered(String tuiton) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_COUNT_BY_TUITON)) {
            statement.setString(1, tuiton);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean isPhoneRegistered(String phone) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_COUNT_BY_PHONE)) {
            statement.setString(1, phone);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean isEmailRegistered(String email) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_COUNT_BY_EMAIL)) {
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