package logic.interfaces;

import logic.DTO.CriterionSelfAssessmentDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ICriterionSelfAssessmentDAO {
    boolean insertCriterionSelfAssessment(CriterionSelfAssessmentDTO criterionSelfAssessment, Connection connection) throws SQLException;

    boolean updateCriterionSelfAssessment(CriterionSelfAssessmentDTO criterionSelfAssessment, Connection connection) throws SQLException;

    boolean deleteCriterionSelfAssessment(String idSelfAssessment, String idCriteria, Connection connection) throws SQLException;

    CriterionSelfAssessmentDTO getCriterionSelfAssessment(String idSelfAssessment, String idCriteria, Connection connection) throws SQLException;

    List<CriterionSelfAssessmentDTO> getAllCriterionSelfAssessments(Connection connection) throws SQLException;
}
