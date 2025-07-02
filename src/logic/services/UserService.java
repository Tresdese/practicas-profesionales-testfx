package logic.services;

import logic.DTO.UserDTO;
import logic.DAO.UserDAO;
import logic.exceptions.RepeatedId;
import logic.exceptions.RepeatedName;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public boolean registerUser(UserDTO user) throws SQLException, IOException, RepeatedId, RepeatedName {

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

    public void updateUser(UserDTO user) throws SQLException, IOException, RepeatedId, RepeatedName {
        boolean success = userDAO.updateUser(user);
        if (!success) {
            throw new SQLException("No se pudo actualizar la organizacion.");
        }
    }

    public boolean updateUserStatus(String idUser, int status) throws SQLException, IOException {
        boolean success = userDAO.updateUserStatus(idUser, status);
        if (!success) {
            throw new SQLException("No se pudo actualizar el estado del usuario.");
        }
        return success;
    }

    public UserDTO searchUserByStaffNumber(String id) throws SQLException, IOException {
        return userDAO.searchUserByStaffNumber(id);
    }

    public String getUserIdByUsername(String username) throws SQLException, IOException {
        return userDAO.getUserIdByUsername(username);
    }

    public List<UserDTO> getAllUsers() throws SQLException, IOException {
        return userDAO.getAllUsers();
    }
}