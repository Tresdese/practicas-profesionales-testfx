package logic.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.SelfAssessmentCriteriaDTO;

public interface ISelfAssessmentCriterialDAO {
    boolean insertSelfAssessmentCriteria(SelfAssessmentCriteriaDTO criteria, Connection connection) throws SQLException;

    boolean updateSelfAssessmentCriteria(SelfAssessmentCriteriaDTO criteria, Connection connection) throws SQLException;

    boolean deleteSelfAssessmentCriteria(SelfAssessmentCriteriaDTO criteria, Connection connection) throws SQLException;

    SelfAssessmentCriteriaDTO getSelfAssessmentCriteria(String idCriteria, Connection connection) throws SQLException;

    List<SelfAssessmentCriteriaDTO> getAllSelfAssessmentCriteria(Connection connection) throws SQLException;
}
