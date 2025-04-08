package logic.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.SelfAssessmentDTO;

public interface ISelfAssessmentDAO {
    boolean insertSelfAssessment(SelfAssessmentDTO selfAssessment, Connection connection) throws SQLException;

    boolean updateSelfAssessment(SelfAssessmentDTO selfAssessment, Connection connection) throws SQLException;

    boolean deleteSelfAssessment(SelfAssessmentDTO selfAssessment, Connection connection) throws SQLException;

    SelfAssessmentDTO getSelfAssessment(String selfAssessmentId, Connection connection) throws SQLException;

    List<SelfAssessmentDTO> getAllSelfAssessments(Connection connection) throws SQLException;
}
