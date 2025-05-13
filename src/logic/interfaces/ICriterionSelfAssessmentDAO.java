package logic.interfaces;

import logic.DTO.CriterionSelfAssessmentDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ICriterionSelfAssessmentDAO {
    boolean insertCriterionSelfAssessment(CriterionSelfAssessmentDTO criterionSelfAssessment) throws SQLException;

    boolean updateCriterionSelfAssessment(CriterionSelfAssessmentDTO criterionSelfAssessment) throws SQLException;

    boolean deleteCriterionSelfAssessment(String idSelfAssessment, String idCriteria) throws SQLException;

    CriterionSelfAssessmentDTO searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(String idSelfAssessment, String idCriteria) throws SQLException;

    List<CriterionSelfAssessmentDTO> getAllCriterionSelfAssessments() throws SQLException;
}
