package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.EvidenceDTO;

public class EvidenceDAO {
    private final static String SQL_INSERT = "INSERT INTO evidencia (idEvidencia, nombreEvidencia, fechaEntrega, ruta, contenido) VALUES (?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE evidencia SET nombreEvidencia = ?, fechaEntrega = ?, ruta = ?, contenido = ? WHERE idEvidencia = ?";
    private final static String SQL_DELETE = "DELETE FROM evidencia WHERE idEvidencia = ?";
    private final static String SQL_SELECT = "SELECT * FROM evidencia WHERE idEvidencia = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM evidencia";

    public boolean insertEvidence(EvidenceDTO evidence, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
            ps.setInt(1, evidence.getIdEvidence());
            ps.setString(2, evidence.getEvidenceName());
            ps.setDate(3, new java.sql.Date(evidence.getDeliveryDate().getTime()));
            ps.setString(4, evidence.getRoute());
            ps.setBytes(5, evidence.getContenido());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateEvidence(EvidenceDTO evidence, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, evidence.getEvidenceName());
            ps.setDate(2, new java.sql.Date(evidence.getDeliveryDate().getTime()));
            ps.setString(3, evidence.getRoute());
            ps.setBytes(4, evidence.getContenido());
            ps.setInt(5, evidence.getIdEvidence());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteEvidence(int idEvidence, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idEvidence);
            return ps.executeUpdate() > 0;
        }
    }

    public EvidenceDTO getEvidence(int idEvidence, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ps.setInt(1, idEvidence);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new EvidenceDTO(rs.getInt("idEvidencia"), rs.getString("nombreEvidencia"),rs.getDate("fechaEntrega"), rs.getString("ruta"), rs.getBytes("contenido"));
                }
            }
        }
        return null;
    }

    public List<EvidenceDTO> getAllEvidences(Connection connection) throws SQLException {
        List<EvidenceDTO> evidences = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                evidences.add(new EvidenceDTO(rs.getInt("idEvidencia"), rs.getString("nombreEvidencia"),
                        rs.getDate("fechaEntrega"), rs.getString("ruta"), rs.getBytes("contenido")));
            }
        }
        return evidences;
    }
}
