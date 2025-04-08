package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.AssessmentCriterionDTO;
import logic.interfaces.IAssessmentCriterionDAO;

public class AssessmentCriterionDAO implements IAssessmentCriterionDAO {
    private final static String SQL_INSERT = "INSERT INTO criterio_evaluacion (idCriterio, nombreCriterio, calificacion) VALUES (?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE criterio_evaluacion SET nombreCriterio = ?, calificacion = ? WHERE idCriterio = ?";
    private final static String SQL_DELETE = "DELETE FROM criterio_evaluacion WHERE idCriterio = ?";
    private final static String SQL_SELECT = "SELECT * FROM criterio_evaluacion WHERE idCriterio = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM criterio_evaluacion";

    public boolean insertAssessmentCriterion(AssessmentCriterionDTO criterion, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
            ps.setString(1, criterion.getIdCriterion());
            ps.setString(2, criterion.getNameCriterion());
            ps.setDouble(3, criterion.getGrade());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateAssessmentCriterion(AssessmentCriterionDTO criterion, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, criterion.getNameCriterion());
            ps.setDouble(2, criterion.getGrade());
            ps.setString(3, criterion.getIdCriterion());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteAssessmentCriterion(String idCriterion, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setString(1, idCriterion);
            return ps.executeUpdate() > 0;
        }
    }

    public AssessmentCriterionDTO getAssessmentCriterion(String idCriterion, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ps.setString(1, idCriterion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AssessmentCriterionDTO(rs.getString("idCriterio"), rs.getString("nombreCriterio"), rs.getDouble("calificacion"));
                }
            }
        }
        return null;
    }

    public List<AssessmentCriterionDTO> getAllAssessmentCriteria(Connection connection) throws SQLException {
        List<AssessmentCriterionDTO> criteria = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                criteria.add(new AssessmentCriterionDTO(rs.getString("idCriterio"), rs.getString("nombreCriterio"), rs.getDouble("calificacion")));
            }
        }
        return criteria;
    }
}
