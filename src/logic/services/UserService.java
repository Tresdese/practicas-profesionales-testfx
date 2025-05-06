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
    private final Connection connection;

    public UserService(Connection connection) {
        this.connection = connection;
        this.userDAO = new UserDAO( connection );
    }

    public boolean registerUser(UserDTO user) throws SQLException, RepeatedId {
        boolean success = false;

        connection.setAutoCommit(false);
        try {
            if (userDAO.isUserRegistered(user.getIdUser())) {
                throw new RepeatedId("El ID del usuario ya está registrado.");
            }

            success = userDAO.insertUser(user);
            if (!success) {
                throw new SQLException("No se pudo registrar el usuario");
            }

            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }

        return success;
    }

    public void updateStudent(LinkedOrganizationDTO organization) throws SQLException {
        boolean success = userDAO.updateLinkedOrganization(organization);
        if (!success) {
            throw new SQLException("No se pudo actualizar la organizacion.");
        }
    }

    public List<LinkedOrganizationDTO> getAllLinkedOrganizations() throws SQLException {
        return userDAO.getAllLinkedOrganizations();
    }

    public LinkedOrganizationDTO searchStudentByTuiton(String id) throws SQLException {
        return userDAO.searchLinkedOrganizationById(id);
    }
}