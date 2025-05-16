package logic.services;

import logic.DTO.UserDTO;
import logic.DAO.UserDAO;
import logic.exceptions.RepeatedId;
import logic.exceptions.RepeatedName;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final UserDAO userDAO;

    public UserService(Connection connection) {
        this.userDAO = new UserDAO();
    }

    public boolean registerUser(UserDTO user) throws SQLException, RepeatedId, RepeatedName {

        if (userDAO.isUserRegistered(user.getIdUser())) {
            throw new RepeatedId("El ID del usuario ya está registrado.");
        }

        if (userDAO.isNameRegistered(user.getUserName())) {
            throw new RepeatedName("El nombre de usuario ya está registrado.");
        }

        boolean success = userDAO.insertUser(user);
        if (!success) {
            throw new SQLException("No se pudo registrar el usuario");
        }

        return success;
    }

    public void updateStudent(UserDTO user) throws SQLException {
        boolean success = userDAO.updateUser(user);
        if (!success) {
            throw new SQLException("No se pudo actualizar la organizacion.");
        }
    }

    public UserDTO searchUserById(String id) throws SQLException {
        return userDAO.searchUserById(id);
    }

    public String getUserIdByUsername(String username) throws SQLException {
        return userDAO.getUserIdByUsername(username);
    }

    public List<UserDTO> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }
}