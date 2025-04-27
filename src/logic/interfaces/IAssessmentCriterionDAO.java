package logic.interfaces;

import logic.DTO.AssessmentCriterionDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IAssessmentCriterionDAO {

    boolean insertAssessmentCriterion(AssessmentCriterionDTO criterion, Connection connection) throws SQLException;

    boolean updateAssessmentCriterion(AssessmentCriterionDTO criterion, Connection connection) throws SQLException;

    boolean deleteAssessmentCriterion(String idCriterion, Connection connection) throws SQLException;

    AssessmentCriterionDTO searchAssessmentCriterionById(String idCriterion, Connection connection) throws SQLException;

    List<AssessmentCriterionDTO> getAllAssessmentCriteria(Connection connection) throws SQLException;
}
