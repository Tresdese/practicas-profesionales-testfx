package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.RepresentativeDTO;

public class RepresentativeDAO {
    private final static String SQL_INSERT = "INSERT INTO representative (idRepresentante, nombres, apellidos, correo, idOrganizacion) VALUES (?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE representative SET nombres = ?, apellidos = ?, correo = ?, idOrganizacion = ? WHERE idRepresentante = ?";
    private final static String SQL_DELETE = "DELETE FROM representative WHERE idRepresentante = ?";
    private final static String SQL_SELECT = "SELECT * FROM representative WHERE idRepresentante = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM representative";

    public void insertRepresentative(RepresentativeDTO representative, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT)) {
            preparedStatement.setString(1, representative.getIdRepresentative());
            preparedStatement.setString(2, representative.getNames());
            preparedStatement.setString(3, representative.getSurnames());
            preparedStatement.setString(4, representative.getEmail());
            preparedStatement.setString(5, representative.getIdOrganization());
            preparedStatement.executeUpdate();
        }
    }

    public void updateRepresentative(RepresentativeDTO representative, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, representative.getNames());
            preparedStatement.setString(2, representative.getSurnames());
            preparedStatement.setString(3, representative.getEmail());
            preparedStatement.setString(4, representative.getIdOrganization());
            preparedStatement.setString(5, representative.getIdRepresentative());
            preparedStatement.executeUpdate();
        }
    }

    public void deleteRepresentative(String idRepresentative, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE)) {
            preparedStatement.setString(1, idRepresentative);
            preparedStatement.executeUpdate();
        }
    }

    public RepresentativeDTO selectRepresentative(String idRepresentative, Connection connection) throws SQLException {
        RepresentativeDTO representative = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT)) {
            preparedStatement.setString(1, idRepresentative);
            ResultSet resultSet = preparedStatement.executeQuery();
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

    public List<RepresentativeDTO> selectAllRepresentatives(Connection connection) throws SQLException {
        List<RepresentativeDTO> representatives = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_ALL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
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
