package logic.services;

import logic.DTO.LinkedOrganizationDTO;
import logic.DAO.LinkedOrganizationDAO;
import logic.exceptions.RepeatedId;
import logic.exceptions.RepeatedName;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class LinkedOrganizationService {
    private final LinkedOrganizationDAO organizationDAO;

    public LinkedOrganizationService(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("La conexión no puede ser nula.");
        }
        this.organizationDAO = new LinkedOrganizationDAO();
    }

    public String registerOrganization(LinkedOrganizationDTO organization) throws SQLException, IOException, RepeatedId, RepeatedName {

        if (organization == null) {
            throw new IllegalArgumentException("La organización no puede ser nula.");
        }

        if (organizationDAO.isLinkedOrganizationRegistered(organization.getIdOrganization())) {
            throw new RepeatedId("El ID de la organización ya está registrado.");
        }

        if (organizationDAO.isNameRegistered(organization.getName())) {
            throw new RepeatedName("El nombre de la organización ya está registrado.");
        }

        String success = organizationDAO.insertLinkedOrganizationAndGetId(organization);
        if (success.isEmpty()) {
            throw new SQLException("No se pudo registrar la organización.");
        }

        return success;
    }

    public void updateOrganization(LinkedOrganizationDTO organization) throws SQLException, IOException, RepeatedId, RepeatedName {
        if (organization == null) {
            throw new IllegalArgumentException("La organización no puede ser nula.");
        }

        boolean success = organizationDAO.updateLinkedOrganization(organization);
        if (!success) {
            throw new SQLException("No se pudo actualizar la organización.");
        }
    }

    public void updateLinkedOrganizationStatus(String idOrganization, int status) throws SQLException, IOException {
        if (idOrganization == null || idOrganization.isEmpty()) {
            throw new IllegalArgumentException("El ID de la organización no puede ser nulo o vacío.");
        }

        boolean success = organizationDAO.updateLinkedOrganizationStatus(idOrganization, status);
        if (!success) {
            throw new SQLException("No se pudo actualizar el estado de la organización.");
        }
    }

    public List<LinkedOrganizationDTO> getAllLinkedOrganizations() throws SQLException, IOException {
        return organizationDAO.getAllLinkedOrganizations();
    }

    public LinkedOrganizationDTO searchLinkedOrganizationById(String id) throws SQLException, IOException {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("El ID no puede ser nulo o vacío.");
        }

        return organizationDAO.searchLinkedOrganizationById(id);
    }

    public LinkedOrganizationDTO searchLinkedOrganizationByName(String name) throws SQLException, IOException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío.");
        }

        return organizationDAO.searchLinkedOrganizationByName(name);
    }
}