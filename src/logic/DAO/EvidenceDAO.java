package logic.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import data_access.ConnectionDataBase;
import logic.DTO.EvidenceDTO;
import logic.interfaces.IEvidenceDAO;

public class EvidenceDAO implements IEvidenceDAO {
    private final static String SQL_INSERT = "INSERT INTO evidencia (idEvidencia, nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE evidencia SET nombreEvidencia = ?, fechaEntrega = ?, ruta = ? WHERE idEvidencia = ?";
    private final static String SQL_DELETE = "DELETE FROM evidencia WHERE idEvidencia = ?";
    private final static String SQL_SELECT = "SELECT * FROM evidencia WHERE idEvidencia = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM evidencia";

    public boolean insertEvidence(EvidenceDTO evidence) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setInt(1, evidence.getIdEvidence());
            statement.setString(2, evidence.getEvidenceName());
            statement.setDate(3, new java.sql.Date(evidence.getDeliveryDate().getTime()));
            statement.setString(4, evidence.getRoute());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateEvidence(EvidenceDTO evidence) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, evidence.getEvidenceName());
            statement.setDate(2, new java.sql.Date(evidence.getDeliveryDate().getTime()));
            statement.setString(3, evidence.getRoute());
            statement.setInt(4, evidence.getIdEvidence());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteEvidence(int idEvidence) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setInt(1, idEvidence);
            return statement.executeUpdate() > 0;
        }
    }

    public EvidenceDTO searchEvidenceById(int idEvidence) throws SQLException {
        EvidenceDTO evidence = new EvidenceDTO(-1, "N/A", java.sql.Timestamp.valueOf("0404-01-01 00:00:00"), "N/A");
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setInt(1, idEvidence);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    evidence = new EvidenceDTO(resultSet.getInt("idEvidencia"), resultSet.getString("nombreEvidencia"),resultSet.getDate("fechaEntrega"), resultSet.getString("ruta"));
                }
            }
        }
        return evidence;
    }

    public List<EvidenceDTO> getAllEvidences() throws SQLException {
        List<EvidenceDTO> evidences = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                evidences.add(new EvidenceDTO(resultSet.getInt("idEvidencia"), resultSet.getString("nombreEvidencia"),
                        resultSet.getDate("fechaEntrega"), resultSet.getString("ruta")));
            }
        }
        return evidences;
    }

    public int getNextEvidenceId() throws SQLException {
        int nextId = 1;
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT MAX(idEvidencia) FROM evidencia")) {
            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }
        }
        return nextId;
    }
}
