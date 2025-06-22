package logic.interfaces;

import logic.DTO.EvaluationDetailDTO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IEvaluationDetailDAO {

    void insertEvaluationDetail(EvaluationDetailDTO detail) throws SQLException, IOException;

    boolean updateEvaluationDetail(EvaluationDetailDTO detail) throws SQLException, IOException;

    boolean deleteEvaluationDetail(int idDetail) throws SQLException, IOException;

    EvaluationDetailDTO searchEvaluationDetailById(int idDetail) throws SQLException, IOException;

    List<EvaluationDetailDTO> getAllEvaluationDetails() throws SQLException, IOException;

}
