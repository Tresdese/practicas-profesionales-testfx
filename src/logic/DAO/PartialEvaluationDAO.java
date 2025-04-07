package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.PartialEvaluationDTO;

public class PartialEvaluationDAO {
    private final static String SQL_INSERT = "INSERT INTO evaluacion_parcial (idEvaluacion, promedio, matricula, IdEvidencia) VALUES (?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE evaluacion_parcial SET promedio = ?, matricula = ?, IdEvidencia = ? WHERE idEvaluacion = ?";
    private final static String SQL_DELETE = "DELETE FROM evaluacion_parcial WHERE idEvaluacion = ?";
    private final static String SQL_SELECT = "SELECT * FROM evaluacion_parcial WHERE idEvaluacion = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM evaluacion_parcial";

    public boolean insertPartialEvaluation(PartialEvaluationDTO evaluation, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
            ps.setString(1, evaluation.getIdEvaluation());
            ps.setDouble(2, evaluation.getAverage());
            ps.setString(3, evaluation.getTuiton());
            ps.setString(4, evaluation.getEvidence());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePartialEvaluation(PartialEvaluationDTO evaluation, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setDouble(1, evaluation.getAverage());
            ps.setString(2, evaluation.getTuiton());
            ps.setString(3, evaluation.getEvidence());
            ps.setString(4, evaluation.getIdEvaluation());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deletePartialEvaluation(String idEvaluation, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setString(1, idEvaluation);
            return ps.executeUpdate() > 0;
        }
    }

    public PartialEvaluationDTO getPartialEvaluation(String idEvaluation, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ps.setString(1, idEvaluation);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PartialEvaluationDTO(rs.getString("idEvaluacion"), rs.getDouble("promedio"), rs.getString("matricula"), rs.getString("IdEvidencia"));
                }
            }
        }
        return null;
    }

    public List<PartialEvaluationDTO> getAllPartialEvaluations(Connection connection) throws SQLException {
        List<PartialEvaluationDTO> evaluations = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                evaluations.add(new PartialEvaluationDTO(rs.getString("idEvaluacion"), rs.getDouble("promedio"), rs.getString("matricula"), rs.getString("IdEvidencia")));
            }
        }
        return evaluations;
    }
}
