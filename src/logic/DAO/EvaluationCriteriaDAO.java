package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.EvaluationCriteriaDTO;
import logic.interfaces.IEvaluationCriteriaDAO;

public class EvaluationCriteriaDAO implements IEvaluationCriteriaDAO {
    private final static String SQL_INSERT = "INSERT INTO evaluacion_criterio (idEvaluacion, idCriterio) VALUES (?, ?)";
    private final static String SQL_UPDATE = "UPDATE evaluacion_criterio SET idCriterio = ? WHERE idEvaluacion = ?";
    private final static String SQL_DELETE = "DELETE FROM evaluacion_criterio WHERE idEvaluacion = ? AND idCriterio = ?";
    private final static String SQL_SELECT = "SELECT * FROM evaluacion_criterio WHERE idEvaluacion = ? AND idCriterio = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM evaluacion_criterio";

    public boolean insertEvaluationCriteria(EvaluationCriteriaDTO criteria, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, criteria.getIdEvaluation());
            statement.setString(2, criteria.getIdCriterion());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateEvaluationCriteria(EvaluationCriteriaDTO criteria, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, criteria.getIdCriterion());
            statement.setString(2, criteria.getIdEvaluation());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteEvaluationCriteria(String idEvaluation, String idCriterion, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, idEvaluation);
            statement.setString(2, idCriterion);
            return statement.executeUpdate() > 0;
        }
    }

    public EvaluationCriteriaDTO getEvaluationCriteria(String idEvaluation, String idCriterion, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, idEvaluation);
            statement.setString(2, idCriterion);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new EvaluationCriteriaDTO(resultSet.getString("idEvaluacion"), resultSet.getString("idCriterio"));
                }
            }
        }
        return null;
    }

    public List<EvaluationCriteriaDTO> getAllEvaluationCriteria(Connection connection) throws SQLException {
        List<EvaluationCriteriaDTO> criteriaList = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                criteriaList.add(new EvaluationCriteriaDTO(resultSet.getString("idEvaluacion"), resultSet.getString("idCriterio")));
            }
        }
        return criteriaList;
    }
}
