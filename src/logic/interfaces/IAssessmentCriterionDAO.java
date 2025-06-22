package logic.interfaces;

import logic.DTO.AssessmentCriterionDTO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IAssessmentCriterionDAO {

    boolean insertAssessmentCriterion(AssessmentCriterionDTO criterion) throws SQLException, IOException;

    boolean updateAssessmentCriterion(AssessmentCriterionDTO criterion) throws SQLException, IOException;

    boolean deleteAssessmentCriterion(String idCriterion) throws SQLException, IOException;

    AssessmentCriterionDTO searchAssessmentCriterionById(String idCriterion) throws SQLException, IOException;

    List<AssessmentCriterionDTO> getAllAssessmentCriteria() throws SQLException, IOException;
}
