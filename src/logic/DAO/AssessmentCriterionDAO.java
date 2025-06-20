package logic.DAO;

import data_access.ConnectionDataBase;
import logic.DTO.AssessmentCriterionDTO;
import logic.interfaces.IAssessmentCriterionDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssessmentCriterionDAO implements IAssessmentCriterionDAO {

    private static final String SQL_INSERT = "INSERT INTO criterio_de_evaluacion (idCriterio, nombreCriterio) VALUES (?, ?)";
    private static final String SQL_UPDATE = "UPDATE criterio_de_evaluacion SET nombreCriterio = ? WHERE idCriterio = ?";
    private static final String SQL_DELETE = "DELETE FROM criterio_de_evaluacion WHERE idCriterio = ?";
    private static final String SQL_SELECT = "SELECT * FROM criterio_de_evaluacion WHERE idCriterio = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM criterio_de_evaluacion";

    public boolean insertAssessmentCriterion(AssessmentCriterionDTO criterion) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, criterion.getIdCriterion());
            statement.setString(2, criterion.getNameCriterion());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateAssessmentCriterion(AssessmentCriterionDTO criterion) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, criterion.getNameCriterion());
            statement.setString(2, criterion.getIdCriterion());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteAssessmentCriterion(String idCriterion) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, idCriterion);
            return statement.executeUpdate() > 0;
        }
    }

    public AssessmentCriterionDTO searchAssessmentCriterionById(String idCriterion) throws SQLException {
        AssessmentCriterionDTO assessmentCriterion = new AssessmentCriterionDTO("N/A", "N/A");
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, idCriterion);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    assessmentCriterion = new AssessmentCriterionDTO(
                            resultSet.getString("idCriterio"),
                            resultSet.getString("nombreCriterio")
                    );
                }
            }
        }
        return assessmentCriterion;
    }

    public List<AssessmentCriterionDTO> getAllAssessmentCriteria() throws SQLException {
        List<AssessmentCriterionDTO> criteria = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                criteria.add(new AssessmentCriterionDTO(
                        resultSet.getString("idCriterio"),
                        resultSet.getString("nombreCriterio")
                ));
            }
        }
        return criteria;
    }
}