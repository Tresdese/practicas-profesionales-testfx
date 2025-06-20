package logic.interfaces;

import logic.DTO.CriterionSelfAssessmentDTO;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface ICriterionSelfAssessmentDAO {
    boolean insertCriterionSelfAssessment(CriterionSelfAssessmentDTO criterionSelfAssessment) throws SQLException;

    boolean updateCriterionSelfAssessment(CriterionSelfAssessmentDTO criterionSelfAssessment) throws SQLException;

    boolean deleteCriterionSelfAssessment(int idSelfAssessment, int idCriteria) throws SQLException;

    CriterionSelfAssessmentDTO searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(int idSelfAssessment, int idCriteria) throws SQLException;

    List<CriterionSelfAssessmentDTO> getAllCriterionSelfAssessments() throws SQLException;

    CriterionSelfAssessmentDTO mapResultSetToDTO(ResultSet rs) throws SQLException;
}
