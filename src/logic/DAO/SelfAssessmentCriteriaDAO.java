package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.SelfAssessmentCriteriaDTO;

public class SelfAssessmentCriteriaDAO {
    private final static String SQL_INSERT = "INSERT INTO autoevaluacion_criterio (idCriterios, nombreCriterio, calificacion) VALUES (?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE autoevaluacion_criterio SET nombreCriterio = ?, calificacion = ? WHERE idCriterio = ?";
    private final static String SQL_DELETE = "DELETE FROM autoevaluacion_criterio WHERE idCriterio = ?";
    private final static String SQL_SELECT = "SELECT * FROM autoevaluacion_criterio WHERE idCriterio = ?";

    public boolean insertSelfAssessmentCriteria(SelfAssessmentCriteriaDTO criteria, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
            ps.setString(1, criteria.getIdCriteria());
            ps.setString(2, criteria.getNameCriteria());
            ps.setDouble(3, criteria.getGrade());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateSelfAssessmentCriteria(SelfAssessmentCriteriaDTO criteria, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, criteria.getNameCriteria());
            ps.setDouble(2, criteria.getGrade());
            ps.setString(3, criteria.getIdCriteria());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteSelfAssessmentCriteria(SelfAssessmentCriteriaDTO criteria, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setString(1, criteria.getIdCriteria());
            return ps.executeUpdate() > 0;
        }
    }

    public SelfAssessmentCriteriaDTO getSelfAssessmentCriteria(String idCriteria, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ps.setString(1, idCriteria);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new SelfAssessmentCriteriaDTO(rs.getString("idCriterios"), rs.getString("nombreCriterio"), rs.getDouble("calificacion"));
                }
            }
        }
        return null;
    }

    public List<SelfAssessmentCriteriaDTO> getAllSelfAssessmentCriteria(Connection connection) throws SQLException {
        List<SelfAssessmentCriteriaDTO> criteriaList = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT * FROM autoevaluacion_criterio";
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                criteriaList.add(new SelfAssessmentCriteriaDTO(rs.getString("idCriterios"), rs.getString("nombreCriterio"), rs.getDouble("calificacion")));
            }
        }
        return criteriaList;
    }
}
