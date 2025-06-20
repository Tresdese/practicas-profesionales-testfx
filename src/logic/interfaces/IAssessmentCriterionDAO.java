package logic.interfaces;

import logic.DTO.AssessmentCriterionDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IAssessmentCriterionDAO {

    boolean insertAssessmentCriterion(AssessmentCriterionDTO criterion) throws SQLException;

    boolean updateAssessmentCriterion(AssessmentCriterionDTO criterion) throws SQLException;

    boolean deleteAssessmentCriterion(String idCriterion) throws SQLException;

    AssessmentCriterionDTO searchAssessmentCriterionById(String idCriterion) throws SQLException;

    List<AssessmentCriterionDTO> getAllAssessmentCriteria() throws SQLException;
}
