package logic.interfaces;

import logic.DTO.PartialEvaluationDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IPartialEvaluationDAO {
    boolean insertPartialEvaluation(PartialEvaluationDTO evaluation) throws SQLException;

    boolean updatePartialEvaluation(PartialEvaluationDTO evaluation) throws SQLException;

    boolean deletePartialEvaluation(String idEvaluation) throws SQLException;

    PartialEvaluationDTO searchPartialEvaluationById(String idEvaluation) throws SQLException;

    List<PartialEvaluationDTO> getAllPartialEvaluations() throws SQLException;
}
