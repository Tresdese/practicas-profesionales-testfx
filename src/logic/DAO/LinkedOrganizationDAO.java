package logic.DAO;

import data_access.ConnectionDataBase;
import logic.DTO.LinkedOrganizationDTO;
import logic.interfaces.ILinkedOrganizationDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LinkedOrganizationDAO implements ILinkedOrganizationDAO {

    private static final String SQL_INSERT = "INSERT INTO organizacion_vinculada (nombre, direccion) VALUES (?, ?)";
    private static final String SQL_UPDATE = "UPDATE organizacion_vinculada SET nombre = ?, direccion = ? WHERE idOrganizacion = ?";
    private static final String SQL_UPDATE_STATUS = "UPDATE organizacion_vinculada SET estado = ? WHERE idOrganizacion = ?";
    private static final String SQL_DELETE = "DELETE FROM organizacion_vinculada WHERE idOrganizacion = ?";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM organizacion_vinculada WHERE idOrganizacion = ?";
    private static final String SQL_SELECT_BY_NAME = "SELECT * FROM organizacion_vinculada WHERE nombre = ?";
    private static final String SQL_SELECT_BY_ADDRESS = "SELECT * FROM organizacion_vinculada WHERE direccion = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM organizacion_vinculada";


    public String insertLinkedOrganizationAndGetId(LinkedOrganizationDTO organization) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, organization.getName());
            statement.setString(2, organization.getAddress());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getString(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID generado para la organización.");
                }
            }
        }
    }

    public boolean updateLinkedOrganization(LinkedOrganizationDTO organization) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, organization.getName());
            statement.setString(2, organization.getAddress());
            statement.setString(3, organization.getIdOrganization());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateLinkedOrganizationStatus(String idOrganization, int status) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_STATUS)) {
            statement.setInt(1, status);
            statement.setString(2, idOrganization);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteLinkedOrganization(String idOrganization) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, idOrganization);
            return statement.executeUpdate() > 0;
        }
    }

    public LinkedOrganizationDTO searchLinkedOrganizationById(String idOrganization) throws SQLException, IOException {
        LinkedOrganizationDTO organization = null;
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            statement.setString(1, idOrganization);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    organization = new LinkedOrganizationDTO(
                            resultSet.getString("idOrganizacion"),
                            resultSet.getString("nombre"),
                            resultSet.getString("direccion"),
                            resultSet.getInt("estado")

                    );
                }
            }
        }
        return organization;
    }

    public LinkedOrganizationDTO searchLinkedOrganizationByName(String name) throws SQLException, IOException {
        LinkedOrganizationDTO organization = null;
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_NAME)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    organization = new LinkedOrganizationDTO(
                            resultSet.getString("idOrganizacion"),
                            resultSet.getString("nombre"),
                            resultSet.getString("direccion"),
                            resultSet.getInt("estado")
                    );
                }
            }
        }
        return organization;
    }

    public boolean isLinkedOrganizationRegistered(String idOrganization) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            statement.setString(1, idOrganization);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public boolean isNameRegistered(String name) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_NAME)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public boolean isAddressRegistered(String address) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ADDRESS)) {
            statement.setString(1, address);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public String getOrganizationNameById(String idOrganization) throws SQLException, IOException {
        String organizationName = null;
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            statement.setString(1, idOrganization);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    organizationName = resultSet.getString("nombre");
                }
            }
        }
        return organizationName;
    }

    public List<LinkedOrganizationDTO> getAllLinkedOrganizations() throws SQLException, IOException {
        List<LinkedOrganizationDTO> organizations = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                organizations.add(new LinkedOrganizationDTO(
                        resultSet.getString("idOrganizacion"),
                        resultSet.getString("nombre"),
                        resultSet.getString("direccion"),
                        resultSet.getInt("estado")
                ));
            }
        }
        return organizations;
    }
}