package data_access.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.EvidenceDTO;

public class EvidenceDAO {
    private final static String SQL_INSERT = "INSERT INTO evidencia (idEvidencia, nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE evidencia SET nombreEvidencia = ?, fechaEntrega = ?, ruta = ? WHERE idEvidencia = ?";
    private final static String SQL_DELETE = "DELETE FROM evidencia WHERE idEvidencia = ?";
    private final static String SQL_SELECT = "SELECT * FROM evidencia WHERE idEvidencia = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM evidencia";

    public boolean insertEvidence(EvidenceDTO evidence, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setInt(1, evidence.getIdEvidence());
            statement.setString(2, evidence.getEvidenceName());
            statement.setDate(3, new java.sql.Date(evidence.getDeliveryDate().getTime()));
            statement.setString(4, evidence.getRoute());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateEvidence(EvidenceDTO evidence, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, evidence.getEvidenceName());
            statement.setDate(2, new java.sql.Date(evidence.getDeliveryDate().getTime()));
            statement.setString(3, evidence.getRoute());
            statement.setInt(4, evidence.getIdEvidence());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteEvidence(int idEvidence, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setInt(1, idEvidence);
            return statement.executeUpdate() > 0;
        }
    }

    public EvidenceDTO getEvidence(int idEvidence, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setInt(1, idEvidence);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new EvidenceDTO(resultSet.getInt("idEvidencia"), resultSet.getString("nombreEvidencia"),resultSet.getDate("fechaEntrega"), resultSet.getString("ruta"));
                }
            }
        }
        return null;
    }

    public List<EvidenceDTO> getAllEvidences(Connection connection) throws SQLException {
        List<EvidenceDTO> evidences = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                evidences.add(new EvidenceDTO(resultSet.getInt("idEvidencia"), resultSet.getString("nombreEvidencia"),
                        resultSet.getDate("fechaEntrega"), resultSet.getString("ruta")));
            }
        }
        return evidences;
    }
}
