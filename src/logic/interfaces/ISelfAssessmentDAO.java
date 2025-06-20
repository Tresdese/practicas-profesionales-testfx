package logic.interfaces;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.SelfAssessmentDTO;

public interface ISelfAssessmentDAO {
    boolean insertSelfAssessment(SelfAssessmentDTO selfAssessment) throws SQLException;

    boolean updateSelfAssessment(SelfAssessmentDTO selfAssessment) throws SQLException;

    boolean deleteSelfAssessment(SelfAssessmentDTO selfAssessment) throws SQLException;

    SelfAssessmentDTO searchSelfAssessmentById(String selfAssessmentId) throws SQLException;

    List<SelfAssessmentDTO> getAllSelfAssessments() throws SQLException;

    int getLastSelfAssessmentId() throws Exception;

    SelfAssessmentDTO mapResultSetToDTO(ResultSet rs) throws SQLException;

    boolean existsSelfAssessment(String matricula, int idProyecto) throws SQLException;
}
