package logic.interfaces;

import logic.DTO.CriterionSelfAssessmentDTO;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface ICriterionSelfAssessmentDAO {
    boolean insertCriterionSelfAssessment(CriterionSelfAssessmentDTO criterionSelfAssessment) throws SQLException, IOException;

    boolean updateCriterionSelfAssessment(CriterionSelfAssessmentDTO criterionSelfAssessment) throws SQLException, IOException;

    boolean deleteCriterionSelfAssessment(int idSelfAssessment, int idCriteria) throws SQLException, IOException;

    CriterionSelfAssessmentDTO searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(int idSelfAssessment, int idCriteria) throws SQLException, IOException;

    List<CriterionSelfAssessmentDTO> getAllCriterionSelfAssessments() throws SQLException, IOException;

    CriterionSelfAssessmentDTO mapResultSetToDTO(ResultSet rs) throws SQLException, IOException;
}
