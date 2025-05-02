package logic.services;

import logic.DTO.LinkedOrganizationDTO;
import logic.DAO.LinkedOrganizationDAO;
import logic.exceptions.RepeatedEmail;
import logic.exceptions.RepeatedId;
import logic.exceptions.RepeatedName;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class LinkedOrganizationService {
    private final LinkedOrganizationDAO organizationDAO;
    private final Connection connection;

    public LinkedOrganizationService(Connection connection) {
        this.connection = connection;
        this.organizationDAO = new LinkedOrganizationDAO(connection);
    }

    public String registerOrganization(LinkedOrganizationDTO organization) throws SQLException, RepeatedId, RepeatedName {
        connection.setAutoCommit(false);
        try {
            if (organizationDAO.isLinkedOrganizationRegistered(organization.getIddOrganization())) {
                throw new RepeatedId("El ID de la organización ya está registrado.");
            }

            if (organizationDAO.isNameRegistered(organization.getName())) {
                throw new RepeatedName("El nombre de la organización ya está registrado.");
            }

            String success = organizationDAO.insertLinkedOrganizationAndGetId(organization);
            if (success.isEmpty()) {
                throw new SQLException("No se pudo registrar la organización.");
            }

            connection.commit();
            return success;
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void updateStudent(LinkedOrganizationDTO organization) throws SQLException {
        boolean success = organizationDAO.updateLinkedOrganization(organization);
        if (!success) {
            throw new SQLException("No se pudo actualizar la organizacion.");
        }
    }

    public List<LinkedOrganizationDTO> getAllLinkedOrganizations() throws SQLException {
        return organizationDAO.getAllLinkedOrganizations();
    }

    public LinkedOrganizationDTO searchStudentByTuiton(String id) throws SQLException {
        return organizationDAO.searchLinkedOrganizationById(id);
    }
}