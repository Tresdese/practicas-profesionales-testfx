package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.UserDTO;
import logic.interfaces.IUserDAO;

public class UserDAO implements IUserDAO {
    private final static String SQL_INSERT = "INSERT INTO usuario (idUsuario, numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE usuario SET numeroDePersonal = ?, nombres = ?, apellidos = ?, nombreUsuario = ?, contraseña = ?, rol = ? WHERE idUsuario = ?";
    private final static String SQL_DELETE = "DELETE FROM usuario WHERE idUsuario = ?";
    private final static String SQL_SELECT = "SELECT * FROM usuario WHERE idUsuario = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM usuario";

    public boolean insertUser(UserDTO user, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
            ps.setString(1, user.getIdUser());
            ps.setString(2, user.getNumberOffStaff());
            ps.setString(3, user.getNames());
            ps.setString(4, user.getSurname());
            ps.setString(5, user.getUserName());
            ps.setString(6, user.getPassword());
            ps.setString(7, user.getRole().toString()); // Assuming Role has a toString() method
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateUser(UserDTO user, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, user.getNumberOffStaff());
            ps.setString(2, user.getNames());
            ps.setString(3, user.getSurname());
            ps.setString(4, user.getUserName());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getRole().toString()); // Assuming Role has a toString() method
            ps.setString(7, user.getIdUser());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteUser(UserDTO user, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setString(1, user.getIdUser());
            return ps.executeUpdate() > 0;
        }
    }

    public UserDTO getUser(String idUser, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ps.setString(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserDTO(
                        rs.getString("idUsuario"),
                        rs.getString("numeroDePersonal"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("nombreUsuario"),
                        rs.getString("contraseña"),
                        null // Assuming you will set the role later
                    );
                }
            }
        }
        return null;
    }

    public List<UserDTO> getAllUsers(Connection connection) throws SQLException {
        List<UserDTO> users = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(new UserDTO(
                    rs.getString("idUsuario"),
                    rs.getString("numeroDePersonal"),
                    rs.getString("nombres"),
                    rs.getString("apellidos"),
                    rs.getString("nombreUsuario"),
                    rs.getString("contraseña"),
                    null // Assuming you will set the role later
                ));
            }
        }
        return users;
    }
}
