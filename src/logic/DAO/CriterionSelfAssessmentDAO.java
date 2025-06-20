package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import data_access.ConnectionDataBase;
import logic.DTO.CriterionSelfAssessmentDTO;
import logic.interfaces.ICriterionSelfAssessmentDAO;

public class CriterionSelfAssessmentDAO implements ICriterionSelfAssessmentDAO {

    private final static String SQL_INSERT = "INSERT INTO autoevaluacion_criterio (idAutoevaluacion, idCriterios, calificacion, comentarios) VALUES (?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE autoevaluacion_criterio SET calificacion = ?, comentarios = ? WHERE idAutoevaluacion = ? AND idCriterios = ?";
    private final static String SQL_DELETE = "DELETE FROM autoevaluacion_criterio WHERE idAutoevaluacion = ? AND idCriterios = ?";
    private final static String SQL_SELECT = "SELECT * FROM autoevaluacion_criterio WHERE idAutoevaluacion = ? AND idCriterios = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM autoevaluacion_criterio";

    public boolean insertCriterionSelfAssessment(CriterionSelfAssessmentDTO criterionSelfAssessment) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setInt(1, criterionSelfAssessment.getIdSelfAssessment());
            statement.setInt(2, criterionSelfAssessment.getIdCriteria());
            statement.setFloat(3, criterionSelfAssessment.getGrade());
            statement.setString(4, criterionSelfAssessment.getComments());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateCriterionSelfAssessment(CriterionSelfAssessmentDTO criterionSelfAssessment) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setFloat(1, criterionSelfAssessment.getGrade());
            statement.setString(2, criterionSelfAssessment.getComments());
            statement.setInt(3, criterionSelfAssessment.getIdSelfAssessment());
            statement.setInt(4, criterionSelfAssessment.getIdCriteria());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteCriterionSelfAssessment(int idSelfAssessment, int idCriteria) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setInt(1, idSelfAssessment);
            statement.setInt(2, idCriteria);
            return statement.executeUpdate() > 0;
        }
    }

    public CriterionSelfAssessmentDTO searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(int idSelfAssessment, int idCriteria) throws SQLException {
        CriterionSelfAssessmentDTO selfAssessment = new CriterionSelfAssessmentDTO(0, 0, -1f, "N/A");
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setInt(1, idSelfAssessment);
            statement.setInt(2, idCriteria);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    selfAssessment = mapResultSetToDTO(resultSet);
                }
            }
        }
        return selfAssessment;
    }

    public List<CriterionSelfAssessmentDTO> getAllCriterionSelfAssessments() throws SQLException {
        List<CriterionSelfAssessmentDTO> criterionSelfAssessments = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                criterionSelfAssessments.add(mapResultSetToDTO(resultSet));
            }
        }
        return criterionSelfAssessments;
    }

    public CriterionSelfAssessmentDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        int idSelfAssessment = rs.getInt("idAutoevaluacion");
        int idCriteria = rs.getInt("idCriterios");
        float grade = rs.getFloat("calificacion");
        String comments = rs.getString("comentarios");
        return new CriterionSelfAssessmentDTO(idSelfAssessment, idCriteria, grade, comments);
    }
}