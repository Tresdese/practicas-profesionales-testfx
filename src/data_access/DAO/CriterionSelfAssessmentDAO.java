package data_access.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.CriterionSelfAssessmentDTO;
import logic.interfaces.ICriterionSelfAssessmentDAO;

public class CriterionSelfAssessmentDAO implements ICriterionSelfAssessmentDAO {
    private final static String SQL_INSERT = "INSERT INTO criterio_autoevaluacion (idAutoevaluacion, idCriterios) VALUES (?, ?)";
    private final static String SQL_UPDATE = "UPDATE criterio_autoevaluacion SET idCriterios = ? WHERE idAutoevaluacion = ?";
    private final static String SQL_DELETE = "DELETE FROM criterio_autoevaluacion WHERE idAutoevaluacion = ? AND idCriterios = ?";
    private final static String SQL_SELECT = "SELECT * FROM criterio_autoevaluacion WHERE idAutoevaluacion = ? AND idCriterios = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM criterio_autoevaluacion";

    public boolean insertCriterionSelfAssessment(CriterionSelfAssessmentDTO criterionSelfAssessment, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, criterionSelfAssessment.getIdSelfAssessment());
            statement.setString(2, criterionSelfAssessment.getIdCriteria());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateCriterionSelfAssessment(CriterionSelfAssessmentDTO criterionSelfAssessment, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, criterionSelfAssessment.getIdCriteria());
            statement.setString(2, criterionSelfAssessment.getIdSelfAssessment());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteCriterionSelfAssessment(String idSelfAssessment, String idCriteria, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, idSelfAssessment);
            statement.setString(2, idCriteria);
            return statement.executeUpdate() > 0;
        }
    }

    public CriterionSelfAssessmentDTO getCriterionSelfAssessment(String idSelfAssessment, String idCriteria, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, idSelfAssessment);
            statement.setString(2, idCriteria);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new CriterionSelfAssessmentDTO(resultSet.getString("idAutoevaluacion"), resultSet.getString("idCriterios"));
                }
            }
        }
        return null;
    }

    public List<CriterionSelfAssessmentDTO> getAllCriterionSelfAssessments(Connection connection) throws SQLException {
        List<CriterionSelfAssessmentDTO> criterionSelfAssessments = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                criterionSelfAssessments.add(new CriterionSelfAssessmentDTO(resultSet.getString("idAutoevaluacion"), resultSet.getString("idCriterios")));
            }
        }
        return criterionSelfAssessments;
    }
}
