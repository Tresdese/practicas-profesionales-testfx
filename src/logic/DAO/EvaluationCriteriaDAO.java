package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.EvaluationCriteriaDTO;

public class EvaluationCriteriaDAO {
    private final static String SQL_INSERT = "INSERT INTO evaluacion_criterio (idEvaluacion, idCriterio) VALUES (?, ?)";
    private final static String SQL_UPDATE = "UPDATE evaluacion_criterio SET idCriterio = ? WHERE idEvaluacion = ?";
    private final static String SQL_DELETE = "DELETE FROM evaluacion_criterio WHERE idEvaluacion = ? AND idCriterio = ?";
    private final static String SQL_SELECT = "SELECT * FROM evaluacion_criterio WHERE idEvaluacion = ? AND idCriterio = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM evaluacion_criterio";

    public boolean insertEvaluationCriteria(EvaluationCriteriaDTO criteria, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
            ps.setString(1, criteria.getIdEvaluation());
            ps.setString(2, criteria.getIdCriterion());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateEvaluationCriteria(EvaluationCriteriaDTO criteria, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, criteria.getIdCriterion());
            ps.setString(2, criteria.getIdEvaluation());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteEvaluationCriteria(String idEvaluation, String idCriterion, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setString(1, idEvaluation);
            ps.setString(2, idCriterion);
            return ps.executeUpdate() > 0;
        }
    }

    public EvaluationCriteriaDTO getEvaluationCriteria(String idEvaluation, String idCriterion, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ps.setString(1, idEvaluation);
            ps.setString(2, idCriterion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new EvaluationCriteriaDTO(rs.getString("idEvaluacion"), rs.getString("idCriterio"));
                }
            }
        }
        return null;
    }

    public List<EvaluationCriteriaDTO> getAllEvaluationCriteria(Connection connection) throws SQLException {
        List<EvaluationCriteriaDTO> criteriaList = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                criteriaList.add(new EvaluationCriteriaDTO(rs.getString("idEvaluacion"), rs.getString("idCriterio")));
            }
        }
        return criteriaList;
    }
}
