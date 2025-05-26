package logic.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.SelfAssessmentCriteriaDTO;

public interface ISelfAssessmentCriterialDAO {
    boolean insertSelfAssessmentCriteria(SelfAssessmentCriteriaDTO criteria) throws SQLException;

    boolean updateSelfAssessmentCriteria(SelfAssessmentCriteriaDTO criteria) throws SQLException;

    boolean deleteSelfAssessmentCriteria(SelfAssessmentCriteriaDTO criteria) throws SQLException;

    SelfAssessmentCriteriaDTO searchSelfAssessmentCriteriaById(String idCriteria) throws SQLException;

    List<SelfAssessmentCriteriaDTO> getAllSelfAssessmentCriteria() throws SQLException;
}
