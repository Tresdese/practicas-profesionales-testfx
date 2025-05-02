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
    private final static String SQL_SELECT_BY_ID = "SELECT * FROM organizacion_vinculada WHERE idOrganizacion = ?";
    private final static String SQL_SELECT_BY_NAME = "SELECT * FROM organizacion_vinculada WHERE nombre = ?";
    private final static String SQL_SELECT_BY_ADDRESS = "SELECT * FROM organizacion_vinculada WHERE direccion = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM organizacion_vinculada";

    public LinkedOrganizationDAO(Connection connection) { this.connection = connection; }

    public String insertLinkedOrganizationAndGetId(LinkedOrganizationDTO organization) throws SQLException {
        String sql = "INSERT INTO organizacion_vinculada (nombre, direccion) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, organization.getName());
            statement.setString(2, organization.getAddress());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getString(1); // Devuelve el ID generado
                } else {
                    throw new SQLException("No se pudo obtener el ID generado para la organización.");
                }
            }
        }
    }

    public boolean updateLinkedOrganization(LinkedOrganizationDTO organization) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, organization.getName());
            statement.setString(2, organization.getAddress());
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
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
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
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            statement.setString(1, idOrganization);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public boolean isNameRegistered(String name) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_NAME)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public boolean isAddressRegistered(String email) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ADDRESS)) {
            statement.setString(1, email);
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
