package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.Role;
import logic.DTO.UserDTO;

public class UserDAO {
    private final static String SQL_INSERT = "INSERT INTO usuario (idUsuario, numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE usuario SET numeroDePersonal = ?, nombres = ?, apellidos = ?, nombreUsuario = ?, contraseña = ?, rol = ? WHERE idUsuario = ?";
    private final static String SQL_UPDATE_STATE = "UPDATE estudiante SET estado = ? WHERE matricula = ?";
    private final static String SQL_DELETE = "DELETE FROM usuario WHERE idUsuario = ?";
    private final static String SQL_SELECT = "SELECT * FROM usuario WHERE idUsuario = ?";
    private final static String SQL_SELECT_BY_ID = "SELECT * FROM usuario WHERE idUsuario = ?";
    private final static String SQL_SELECT_BY_USERNAME = "SELECT * FROM usuario WHERE nombreUsuario = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM usuario";

    public boolean insertUser(UserDTO user, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, user.getIdUser());
            statement.setString(2, user.getNumberOffStaff());
            statement.setString(3, user.getNames());
            statement.setString(4, user.getSurname());
            statement.setString(5, user.getUserName());
            statement.setString(6, user.getPassword());
            statement.setString(7, user.getRole().toString()); 
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
            statement.setString(6, user.getRole().toString()); 
            statement.setString(7, user.getIdUser());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateUserState(String idUser, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_STATE)) {
            statement.setInt(1, 1);
            statement.setString(2, idUser);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteUser(UserDTO user, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, user.getIdUser());
            return statement.executeUpdate() > 0;
        }
    }

    public UserDTO getUser(UserDTO user, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, user.getIdUser());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new UserDTO(
                        resultSet.getString("idUsuario"),
                        resultSet.getString("numeroDePersonal"),
                        resultSet.getString("nombres"),
                        resultSet.getString("apellidos"),
                        resultSet.getString("nombreUsuario"),
                        resultSet.getString("contraseña"),
                        user.getRole()
                    );
                }
            }
        }
        return null;
    }

    public boolean idIsRegistered(String idUser, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            statement.setString(1, idUser);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean isUserNameRegistered (String username, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_USERNAME)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
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
                    Role.valueOf(resultSet.getString("rol"))
                ));
            }
        }
        return users;
    }

    public UserDTO searchUserById(String idUser, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
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
                        Role.valueOf(resultSet.getString("rol"))
                    );
                }
            }
        }
        return null;
    }
}
