package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import data_access.ConecctionDataBase;
import logic.DTO.Role;
import logic.DTO.UserDTO;
import logic.interfaces.IUserDAO;

public class UserDAO implements IUserDAO { // TODO implementar la interfaz IUserDAO

    private static final String SQL_INSERT = "INSERT INTO usuario (idUsuario, numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE usuario SET numeroDePersonal = ?, nombres = ?, apellidos = ?, nombreUsuario = ?, contraseña = ?, rol = ? WHERE idUsuario = ?";
    private static final String SQL_DELETE = "DELETE FROM usuario WHERE idUsuario = ?";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM usuario WHERE idUsuario = ?";
    private static final String SQL_SELECT_BY_USERNAME = "SELECT * FROM usuario WHERE nombreUsuario = ?";
    private static final String SQL_SELECT_BY_USER_AND_PASSWORD = "SELECT * FROM usuario WHERE nombreUsuario = ? AND contraseña = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM usuario";

    public boolean insertUser(UserDTO user) throws SQLException {
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase();) {
            Connection connection = connectionDataBase.connectDB();
            PreparedStatement statement = connection.prepareStatement(SQL_INSERT);
            statement.setString(1, user.getIdUser());
            statement.setString(2, user.getStaffNumber());
            statement.setString(3, user.getNames());
            statement.setString(4, user.getSurnames());
            statement.setString(5, user.getUserName());
            statement.setString(6, user.getPassword());
            statement.setString(7, user.getRole().toString());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateUser(UserDTO user) throws SQLException {
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase()) {
            Connection connection = connectionDataBase.connectDB();
            PreparedStatement statement = connection.prepareStatement(SQL_UPDATE);
            statement.setString(1, user.getStaffNumber());
            statement.setString(2, user.getNames());
            statement.setString(3, user.getSurnames());
            statement.setString(4, user.getUserName());
            statement.setString(5, user.getPassword());
            statement.setString(6, user.getRole().toString());
            statement.setString(7, user.getIdUser());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteUser(String idUser) throws SQLException {
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase()) {
            Connection connection = connectionDataBase.connectDB();
            PreparedStatement statement = connection.prepareStatement(SQL_DELETE);
            statement.setString(1, idUser);
            return statement.executeUpdate() > 0;
        }
    }

    public UserDTO searchUserById(String idUser) throws SQLException {
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase()) {
            Connection connection = connectionDataBase.connectDB();
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID);
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
                            Role.valueOf(resultSet.getString("rol").toUpperCase())
                    );
                }
            }
        }
        return null;
    }

    public UserDTO searchUserByUsernameAndPassword(String username, String hashedPassword) throws SQLException {
        UserDTO user = new UserDTO("INVALID", "INVALID", "INVALID", "INVALID", "INVALID", "INVALID", Role.GUEST);
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase()) {
            Connection connection = connectionDataBase.connectDB();
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_USER_AND_PASSWORD);
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    user = new UserDTO(
                            resultSet.getString("idUsuario"),
                            resultSet.getString("numeroDePersonal"),
                            resultSet.getString("nombres"),
                            resultSet.getString("apellidos"),
                            resultSet.getString("nombreUsuario"),
                            resultSet.getString("contraseña"),
                            Role.valueOf(resultSet.getString("rol").toUpperCase())
                    );
                }
            }
        }
        return user;
    }

    public boolean isUserRegistered(String idUser) throws SQLException {
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase()) {
            Connection connection = connectionDataBase.connectDB();
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID);
            statement.setString(1, idUser);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public boolean isNameRegistered(String username) throws SQLException {
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase()) {
            Connection connection = connectionDataBase.connectDB();
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_USERNAME);
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public String getUserIdByUsername(String username) throws SQLException {
        String userId = null;
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase()) {
            Connection connection = connectionDataBase.connectDB();
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_USERNAME);
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    userId = resultSet.getString("idUsuario");
                }
            }
        }
        return userId;
    }

    public List<UserDTO> getAllUsers() throws SQLException {
        List<UserDTO> users = new ArrayList<>();
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase()) {
            Connection connection = connectionDataBase.connectDB();
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(new UserDTO(
                        resultSet.getString("idUsuario"),
                        resultSet.getString("numeroDePersonal"),
                        resultSet.getString("nombres"),
                        resultSet.getString("apellidos"),
                        resultSet.getString("nombreUsuario"),
                        resultSet.getString("contraseña"),
                        Role.valueOf(resultSet.getString("rol").toUpperCase())
                ));
            }
        }
        return users;
    }
}