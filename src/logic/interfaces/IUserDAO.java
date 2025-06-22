package logic.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.UserDTO;


public interface IUserDAO {
    boolean insertUser(UserDTO user) throws SQLException;

    boolean updateUser(UserDTO user) throws SQLException;

    boolean updateUserStatus(String idUser, int status) throws SQLException;

    boolean deleteUser(String idUser) throws SQLException;

    UserDTO searchUserById(String idUser) throws SQLException;

    UserDTO searchUserByUsernameAndPassword(String username, String hashedPassword) throws SQLException;

    boolean isUserRegistered(String idUser) throws SQLException;

    boolean isNameRegistered(String username) throws SQLException;

    List<UserDTO> getAllUsers() throws SQLException;
}
