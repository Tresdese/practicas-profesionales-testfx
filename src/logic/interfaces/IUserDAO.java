package logic.interfaces;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.UserDTO;


public interface IUserDAO {
    boolean insertUser(UserDTO user) throws SQLException, IOException;

    boolean updateUser(UserDTO user) throws SQLException, IOException;

    boolean updateUserStatus(String idUser, int status) throws SQLException, IOException;

    boolean deleteUser(String idUser) throws SQLException, IOException;

    String getUserIdByUsername(String username) throws SQLException, IOException;

    UserDTO searchUserByStaffNumber(String idUser) throws SQLException, IOException;

    UserDTO searchUserByUsernameAndPassword(String username, String hashedPassword) throws SQLException, IOException;

    boolean isUserRegistered(String idUser) throws SQLException, IOException;

    boolean isNameRegistered(String username) throws SQLException, IOException;

    List<UserDTO> getAllUsers() throws SQLException, IOException;
}
