package logic.interfaces;

import logic.DTO.EvaluationPresentationDTO;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public interface IEvaluationPresentationDAO {

    int insertEvaluationPresentation(EvaluationPresentationDTO evaluation) throws SQLException, IOException;

    boolean updateEvaluationPresentation(EvaluationPresentationDTO evaluation) throws SQLException, IOException;

    boolean deleteEvaluationPresentation(int idEvaluation) throws SQLException, IOException;

    EvaluationPresentationDTO searchEvaluationPresentationById(int idEvaluation) throws SQLException, IOException;

    List<EvaluationPresentationDTO> getAllEvaluationPresentations() throws SQLException, IOException;

    int getLastInsertedId() throws SQLException, IOException;

    List<EvaluationPresentationDTO> getEvaluationPresentationsByTuition(String tuiton) throws SQLException, IOException;

    List<EvaluationPresentationDTO> getEvaluationPresentationsByDate(Date date) throws SQLException, IOException;

}
