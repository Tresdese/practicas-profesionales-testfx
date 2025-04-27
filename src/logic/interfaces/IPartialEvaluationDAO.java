package logic.interfaces;

import logic.DTO.PartialEvaluationDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IPartialEvaluationDAO {
    boolean insertPartialEvaluation(PartialEvaluationDTO evaluation, Connection connection) throws SQLException;

    boolean updatePartialEvaluation(PartialEvaluationDTO evaluation, Connection connection) throws SQLException;

    boolean deletePartialEvaluation(String idEvaluation, Connection connection) throws SQLException;

    PartialEvaluationDTO searchPartialEvaluationById(String idEvaluation, Connection connection) throws SQLException;

    List<PartialEvaluationDTO> getAllPartialEvaluations(Connection connection) throws SQLException;
}
