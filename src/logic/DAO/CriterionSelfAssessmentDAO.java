package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.CriterionSelfAssessmentDTO;

public class CriterionSelfAssessmentDAO {
    private final static String SQL_INSERT = "INSERT INTO criterio_autoevaluacion (idAutoevaluacion, idCriterios) VALUES (?, ?)";
    private final static String SQL_UPDATE = "UPDATE criterio_autoevaluacion SET idCriterios = ? WHERE idAutoevaluacion = ?";
    private final static String SQL_DELETE = "DELETE FROM criterio_autoevaluacion WHERE idAutoevaluacion = ? AND idCriterios = ?";
    private final static String SQL_SELECT = "SELECT * FROM criterio_autoevaluacion WHERE idAutoevaluacion = ? AND idCriterios = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM criterio_autoevaluacion";

    public boolean insertCriterionSelfAssessment(CriterionSelfAssessmentDTO criterionSelfAssessment, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
            ps.setString(1, criterionSelfAssessment.getIdSelfAssessment());
            ps.setString(2, criterionSelfAssessment.getIdCriteria());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateCriterionSelfAssessment(CriterionSelfAssessmentDTO criterionSelfAssessment, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, criterionSelfAssessment.getIdCriteria());
            ps.setString(2, criterionSelfAssessment.getIdSelfAssessment());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteCriterionSelfAssessment(String idSelfAssessment, String idCriteria, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setString(1, idSelfAssessment);
            ps.setString(2, idCriteria);
            return ps.executeUpdate() > 0;
        }
    }

    public CriterionSelfAssessmentDTO getCriterionSelfAssessment(String idSelfAssessment, String idCriteria, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ps.setString(1, idSelfAssessment);
            ps.setString(2, idCriteria);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CriterionSelfAssessmentDTO(rs.getString("idAutoevaluacion"), rs.getString("idCriterios"));
                }
            }
        }
        return null;
    }

    public List<CriterionSelfAssessmentDTO> getAllCriterionSelfAssessments(Connection connection) throws SQLException {
        List<CriterionSelfAssessmentDTO> criterionSelfAssessments = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                criterionSelfAssessments.add(new CriterionSelfAssessmentDTO(rs.getString("idAutoevaluacion"), rs.getString("idCriterios")));
            }
        }
        return criterionSelfAssessments;
    }
}
