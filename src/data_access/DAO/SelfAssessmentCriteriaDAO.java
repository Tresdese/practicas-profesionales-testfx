package data_access.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.SelfAssessmentCriteriaDTO;
import logic.interfaces.ISelfAssessmentCriterialDAO;

public class SelfAssessmentCriteriaDAO implements ISelfAssessmentCriterialDAO {
    private final static String SQL_INSERT = "INSERT INTO criterio_de_autoevaluacion (idCriterios, nombreCriterio, calificacion) VALUES (?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE criterio_de_autoevaluacion SET nombreCriterio = ?, calificacion = ? WHERE idCriterios = ?";
    private final static String SQL_DELETE = "DELETE FROM criterio_de_autoevaluacion WHERE idCriterios = ?";
    private final static String SQL_SELECT = "SELECT * FROM criterio_de_autoevaluacion WHERE idCriterios = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM criterio_de_autoevaluacion";

    public boolean insertSelfAssessmentCriteria(SelfAssessmentCriteriaDTO criteria, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, criteria.getIdCriteria());
            statement.setString(2, criteria.getNameCriteria());
            statement.setDouble(3, criteria.getGrade());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateSelfAssessmentCriteria(SelfAssessmentCriteriaDTO criteria, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, criteria.getNameCriteria());
            statement.setDouble(2, criteria.getGrade());
            statement.setString(3, criteria.getIdCriteria());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteSelfAssessmentCriteria(SelfAssessmentCriteriaDTO criteria, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, criteria.getIdCriteria());
            return statement.executeUpdate() > 0;
        }
    }

    public SelfAssessmentCriteriaDTO getSelfAssessmentCriteria(String idCriteria, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, idCriteria);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new SelfAssessmentCriteriaDTO(
                            resultSet.getString("idCriterios"),
                            resultSet.getString("nombreCriterio"),
                            resultSet.getDouble("calificacion")
                    );
                }
            }
        }
        return null;
    }

    public List<SelfAssessmentCriteriaDTO> getAllSelfAssessmentCriteria(Connection connection) throws SQLException {
        List<SelfAssessmentCriteriaDTO> criteriaList = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                criteriaList.add(new SelfAssessmentCriteriaDTO(
                        resultSet.getString("idCriterios"),
                        resultSet.getString("nombreCriterio"),
                        resultSet.getDouble("calificacion")
                ));
            }
        }
        return criteriaList;
    }
}