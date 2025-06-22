package logic.interfaces;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.SelfAssessmentDTO;

public interface ISelfAssessmentDAO {
    boolean insertSelfAssessment(SelfAssessmentDTO selfAssessment) throws SQLException, IOException;

    boolean updateSelfAssessment(SelfAssessmentDTO selfAssessment) throws SQLException, IOException;

    boolean deleteSelfAssessment(SelfAssessmentDTO selfAssessment) throws SQLException, IOException;

    SelfAssessmentDTO searchSelfAssessmentById(String selfAssessmentId) throws SQLException, IOException;

    List<SelfAssessmentDTO> getAllSelfAssessments() throws SQLException, IOException;

    int getLastSelfAssessmentId() throws Exception, SQLException, IOException;

    SelfAssessmentDTO mapResultSetToDTO(ResultSet rs) throws SQLException, IOException;

    boolean existsSelfAssessment(String matricula, int idProyecto) throws SQLException, IOException;
}
