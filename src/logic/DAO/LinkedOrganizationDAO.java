package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.LinkedOrganizationDTO;
import logic.interfaces.ILinkedOrganizationDAO;

public class LinkedOrganizationDAO implements ILinkedOrganizationDAO {
    private final Connection connection;

    private final static String SQL_INSERT = "INSERT INTO organizacion_vinculada (idOrganizacion, nombre, direccion) VALUES (?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE organizacion_vinculada SET nombre = ?, direccion = ? WHERE idOrganizacion = ?";
    private final static String SQL_DELETE = "DELETE FROM organizacion_vinculada WHERE idOrganizacion = ?";
    private final static String SQL_SELECT = "SELECT * FROM organizacion_vinculada WHERE idOrganizacion = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM organizacion_vinculada";

    public LinkedOrganizationDAO(Connection connection) { this.connection = connection; }

    public boolean insertLinkedOrganization(LinkedOrganizationDTO organization) throws SQLException {
        LinkedOrganizationDTO existingOrganization = searchLinkedOrganizationById(organization.getIddOrganization());
        if (existingOrganization != null) {
            return organization.getIddOrganization().equals(existingOrganization.getIddOrganization());
        }

        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, organization.getIddOrganization());
            statement.setString(2, organization.getName());
            statement.setString(3, organization.getAdddress());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateLinkedOrganization(LinkedOrganizationDTO organization) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, organization.getName());
            statement.setString(2, organization.getAdddress());
            statement.setString(3, organization.getIddOrganization());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteLinkedOrganization(String idOrganization) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, idOrganization);
            return statement.executeUpdate() > 0;
        }
    }

    public LinkedOrganizationDTO searchLinkedOrganizationById(String idOrganization) throws SQLException {
        LinkedOrganizationDTO group = new LinkedOrganizationDTO("N/A", "N/A", "N/A");
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, idOrganization);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    group = new LinkedOrganizationDTO(resultSet.getString("idOrganizacion"), resultSet.getString("nombre"), resultSet.getString("direccion"));
                }
            }
        }
        return group;
    }

    public boolean isLinkedOrganizationRegistered(String idOrganization) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, idOrganization);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public List<LinkedOrganizationDTO> getAllLinkedOrganizations() throws SQLException {
        List<LinkedOrganizationDTO> organizations = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                organizations.add(new LinkedOrganizationDTO(resultSet.getString("idOrganizacion"), resultSet.getString("nombre"), resultSet.getString("direccion")));
            }
        }
        return organizations;
    }
}
