package logic.interfaces;

import logic.DTO.EvaluationDetailDTO;

import java.sql.SQLException;
import java.util.List;

public interface IEvaluationDetailDAO {

    void insertEvaluationDetail(EvaluationDetailDTO detail) throws SQLException;

    boolean updateEvaluationDetail(EvaluationDetailDTO detail) throws SQLException;

    boolean deleteEvaluationDetail(int idDetail) throws SQLException;

    EvaluationDetailDTO searchEvaluationDetailById(int idDetail) throws SQLException;

    List<EvaluationDetailDTO> getAllEvaluationDetails() throws SQLException;

}
