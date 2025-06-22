package logic.interfaces;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.SelfAssessmentCriteriaDTO;

public interface ISelfAssessmentCriterialDAO {
    boolean insertSelfAssessmentCriteria(SelfAssessmentCriteriaDTO criteria) throws SQLException, IOException;

    boolean updateSelfAssessmentCriteria(SelfAssessmentCriteriaDTO criteria) throws SQLException, IOException;

    boolean deleteSelfAssessmentCriteria(SelfAssessmentCriteriaDTO criteria) throws SQLException, IOException;

    SelfAssessmentCriteriaDTO searchSelfAssessmentCriteriaById(String idCriteria) throws SQLException, IOException;

    List<SelfAssessmentCriteriaDTO> getAllSelfAssessmentCriteria() throws SQLException, IOException;
}
