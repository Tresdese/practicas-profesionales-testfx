package logic.interfaces;

import logic.DTO.EvaluationCriteriaDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IEvaluationCriteriaDAO {
    boolean insertEvaluationCriteria(EvaluationCriteriaDTO criteria, Connection connection) throws SQLException;

    boolean updateEvaluationCriteria(EvaluationCriteriaDTO criteria, Connection connection) throws SQLException;

    boolean deleteEvaluationCriteria(String idEvaluation, String idCriterion, Connection connection) throws SQLException;

    EvaluationCriteriaDTO searchEvaluationCriteriaById(String idEvaluation, String idCriterion, Connection connection) throws SQLException;

    List<EvaluationCriteriaDTO> getAllEvaluationCriteria(Connection connection) throws SQLException;
}
