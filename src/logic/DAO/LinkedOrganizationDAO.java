package logic.DAO;

import data_access.ConecctionDataBase;
import logic.DTO.LinkedOrganizationDTO;
import logic.interfaces.ILinkedOrganizationDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LinkedOrganizationDAO implements ILinkedOrganizationDAO {
    private final Connection connection;

    private static final String SQL_INSERT = "INSERT INTO organizacion_vinculada (nombre, direccion) VALUES (?, ?)";
    private static final String SQL_UPDATE = "UPDATE organizacion_vinculada SET nombre = ?, direccion = ? WHERE idOrganizacion = ?";
    private static final String SQL_DELETE = "DELETE FROM organizacion_vinculada WHERE idOrganizacion = ?";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM organizacion_vinculada WHERE idOrganizacion = ?";
    private static final String SQL_SELECT_BY_NAME = "SELECT * FROM organizacion_vinculada WHERE nombre = ?";
    private static final String SQL_SELECT_BY_ADDRESS = "SELECT * FROM organizacion_vinculada WHERE direccion = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM organizacion_vinculada";

    public LinkedOrganizationDAO(Connection connection) {
        this.connection = connection;
    }

    public String insertLinkedOrganizationAndGetId(LinkedOrganizationDTO organization) throws SQLException {
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase();
             Connection connection = connectionDataBase.connectDB();
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

    public boolean updateLinkedOrganization(LinkedOrganizationDTO organization) throws SQLException {
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, organization.getName());
            statement.setString(2, organization.getAddress());
            statement.setString(3, organization.getIddOrganization());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteLinkedOrganization(String idOrganization) throws SQLException {
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, idOrganization);
            return statement.executeUpdate() > 0;
        }
    }

    public LinkedOrganizationDTO searchLinkedOrganizationById(String idOrganization) throws SQLException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO("N/A", "N/A", "N/A");
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            statement.setString(1, idOrganization);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    organization = new LinkedOrganizationDTO(
                            resultSet.getString("idOrganizacion"),
                            resultSet.getString("nombre"),
                            resultSet.getString("direccion")
                    );
                }
            }
        }
        return organization;
    }

    public LinkedOrganizationDTO searchLinkedOrganizationByName(String name) throws SQLException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO("N/A", "N/A", "N/A");
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_NAME)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    organization = new LinkedOrganizationDTO(
                            resultSet.getString("idOrganizacion"),
                            resultSet.getString("nombre"),
                            resultSet.getString("direccion")
                    );
                }
            }
        }
        return organization;
    }

    public boolean isLinkedOrganizationRegistered(String idOrganization) throws SQLException {
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            statement.setString(1, idOrganization);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public boolean isNameRegistered(String name) throws SQLException {
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_NAME)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public boolean isAddressRegistered(String address) throws SQLException {
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ADDRESS)) {
            statement.setString(1, address);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public List<LinkedOrganizationDTO> getAllLinkedOrganizations() throws SQLException {
        List<LinkedOrganizationDTO> organizations = new ArrayList<>();
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                organizations.add(new LinkedOrganizationDTO(
                        resultSet.getString("idOrganizacion"),
                        resultSet.getString("nombre"),
                        resultSet.getString("direccion")
                ));
            }
        }
        return organizations;
    }
}