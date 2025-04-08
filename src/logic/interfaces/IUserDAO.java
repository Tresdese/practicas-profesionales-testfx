package logic.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.UserDTO;


public interface IUserDAO {
    boolean insertUser(UserDTO user, Connection connection) throws SQLException;

    boolean updateUser(UserDTO user, Connection connection) throws SQLException;

    boolean deleteUser(UserDTO user, Connection connection) throws SQLException;

    UserDTO getUser(String idUser, Connection connection) throws SQLException;

    List<UserDTO> getAllUsers(Connection connection) throws SQLException;
}
