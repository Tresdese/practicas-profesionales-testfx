package data_access.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.UserDTO;

public class UserDAO {
    private final static String SQL_INSERT = "INSERT INTO usuario (idUsuario, numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE usuario SET numeroDePersonal = ?, nombres = ?, apellidos = ?, nombreUsuario = ?, contraseña = ?, rol = ? WHERE idUsuario = ?";
    private final static String SQL_DELETE = "DELETE FROM usuario WHERE idUsuario = ?";
    private final static String SQL_SELECT = "SELECT * FROM usuario WHERE idUsuario = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM usuario";

    public boolean insertUser(UserDTO user, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, user.getIdUser());
            statement.setString(2, user.getNumberOffStaff());
            statement.setString(3, user.getNames());
            statement.setString(4, user.getSurname());
            statement.setString(5, user.getUserName());
            statement.setString(6, user.getPassword());
            statement.setString(7, user.getRole().toString()); // Assuming Role has a toString() method
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateUser(UserDTO user, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, user.getNumberOffStaff());
            statement.setString(2, user.getNames());
            statement.setString(3, user.getSurname());
            statement.setString(4, user.getUserName());
            statement.setString(5, user.getPassword());
            statement.setString(6, user.getRole().toString()); // Assuming Role has a toString() method
            statement.setString(7, user.getIdUser());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteUser(UserDTO user, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, user.getIdUser());
            return statement.executeUpdate() > 0;
        }
    }

    public UserDTO getUser(String idUser, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, idUser);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new UserDTO(
                        resultSet.getString("idUsuario"),
                        resultSet.getString("numeroDePersonal"),
                        resultSet.getString("nombres"),
                        resultSet.getString("apellidos"),
                        resultSet.getString("nombreUsuario"),
                        resultSet.getString("contraseña"),
                        null // Assuming you will set the role later
                    );
                }
            }
        }
        return null;
    }

    public List<UserDTO> getAllUsers(Connection connection) throws SQLException {
        List<UserDTO> users = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(new UserDTO(
                    resultSet.getString("idUsuario"),
                    resultSet.getString("numeroDePersonal"),
                    resultSet.getString("nombres"),
                    resultSet.getString("apellidos"),
                    resultSet.getString("nombreUsuario"),
                    resultSet.getString("contraseña"),
                    null // Assuming you will set the role later
                ));
            }
        }
        return users;
    }
}
