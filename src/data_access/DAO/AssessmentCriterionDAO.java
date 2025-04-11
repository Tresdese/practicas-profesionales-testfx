package data_access.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.AssessmentCriterionDTO;
import logic.interfaces.IAssessmentCriterionDAO;

public class AssessmentCriterionDAO implements IAssessmentCriterionDAO {
    private final static String SQL_INSERT = "INSERT INTO criterio_de_evaluacion (idCriterio, nombreCriterio, calificacion) VALUES (?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE criterio_de_evaluacion SET nombreCriterio = ?, calificacion = ? WHERE idCriterio = ?";
    private final static String SQL_DELETE = "DELETE FROM criterio_de_evaluacion WHERE idCriterio = ?";
    private final static String SQL_SELECT = "SELECT * FROM criterio_de_evaluacion WHERE idCriterio = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM criterio_de_evaluacion";

    public boolean insertAssessmentCriterion(AssessmentCriterionDTO criterion, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, criterion.getIdCriterion());
            statement.setString(2, criterion.getNameCriterion());
            statement.setDouble(3, criterion.getGrade());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateAssessmentCriterion(AssessmentCriterionDTO criterion, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, criterion.getNameCriterion());
            statement.setDouble(2, criterion.getGrade());
            statement.setString(3, criterion.getIdCriterion());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteAssessmentCriterion(String idCriterion, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, idCriterion);
            return statement.executeUpdate() > 0;
        }
    }

    public AssessmentCriterionDTO getAssessmentCriterion(String idCriterion, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, idCriterion);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new AssessmentCriterionDTO(resultSet.getString("idCriterio"), resultSet.getString("nombreCriterio"), resultSet.getDouble("calificacion"));
                }
            }
        }
        return null;
    }

    public List<AssessmentCriterionDTO> getAllAssessmentCriteria(Connection connection) throws SQLException {
        List<AssessmentCriterionDTO> criteria = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                criteria.add(new AssessmentCriterionDTO(resultSet.getString("idCriterio"), resultSet.getString("nombreCriterio"), resultSet.getDouble("calificacion")));
            }
        }
        return criteria;
    }
}
