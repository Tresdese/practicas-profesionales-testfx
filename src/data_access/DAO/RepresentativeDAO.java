package data_access.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.RepresentativeDTO;
import logic.interfaces.IRepresentativeDAO;

public class RepresentativeDAO implements IRepresentativeDAO {
    private final static String SQL_INSERT = "INSERT INTO representante (idRepresentante, nombres, apellidos, correo, idOrganizacion) VALUES (?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE representante SET nombres = ?, apellidos = ?, correo = ?, idOrganizacion = ? WHERE idRepresentante = ?";
    private final static String SQL_DELETE = "DELETE FROM representante WHERE idRepresentante = ?";
    private final static String SQL_SELECT = "SELECT * FROM representante WHERE idRepresentante = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM representante";

    public boolean insertRepresentative(RepresentativeDTO representative, Connection connection) throws SQLException {
        RepresentativeDTO existingRepresentative = getRepresentative(representative.getIdRepresentative(), connection);
        if (existingRepresentative != null) {
            return representative.getIdRepresentative().equals(existingRepresentative.getIdRepresentative());
        }

        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, representative.getIdRepresentative());
            statement.setString(2, representative.getNames());
            statement.setString(3, representative.getSurnames());
            statement.setString(4, representative.getEmail());
            statement.setString(5, representative.getIdOrganization());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateRepresentative(RepresentativeDTO representative, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, representative.getNames());
            statement.setString(2, representative.getSurnames());
            statement.setString(3, representative.getEmail());
            statement.setString(4, representative.getIdOrganization());
            statement.setString(5, representative.getIdRepresentative());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteRepresentative(String idRepresentative, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, idRepresentative);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public RepresentativeDTO getRepresentative(String idRepresentative, Connection connection) throws SQLException {
        RepresentativeDTO representative = null;
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, idRepresentative);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                representative = new RepresentativeDTO(
                        resultSet.getString("idRepresentante"),
                        resultSet.getString("nombres"),
                        resultSet.getString("apellidos"),
                        resultSet.getString("correo"),
                        resultSet.getString("idOrganizacion")
                );
            }
        }
        return representative;
    }

    @Override
    public List<RepresentativeDTO> getAllRepresentatives(Connection connection) throws SQLException {
        List<RepresentativeDTO> representatives = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                RepresentativeDTO representative = new RepresentativeDTO(
                        resultSet.getString("idRepresentante"),
                        resultSet.getString("nombres"),
                        resultSet.getString("apellidos"),
                        resultSet.getString("correo"),
                        resultSet.getString("idOrganizacion")
                );
                representatives.add(representative);
            }
        }
        return representatives;
    }
}