package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.SelfAssessmentDTO;

public class SelfAssessmentDAO {
    private final static String SQL_INSERT = "INSERT INTO autoevaluacion (idAutoevaluacion, comentarios, calificacion, matricula, idEvidencia) VALUES (?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE autoevaluacion SET comentarios = ?, calificacion = ?, matricula = ?, idEvidencia = ? WHERE idAutoevaluacion = ?";
    private final static String SQL_DELETE = "DELETE FROM autoevaluacion WHERE idAutoevaluacion = ?";
    private final static String SQL_SELECT = "SELECT * FROM autoevaluacion WHERE idAutoevaluacion = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM autoevaluacion";

    public boolean insertSelfAssessment(SelfAssessmentDTO selfAssessment, Connection connection) throws SQLException {
        SelfAssessmentDTO existingSelfAssessment = getSelfAssessment(selfAssessment.getSelfAssessmentId(), connection);
        if (existingSelfAssessment != null) {
            return selfAssessment.getSelfAssessmentId().equals(existingSelfAssessment.getSelfAssessmentId());
        }

        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, selfAssessment.getSelfAssessmentId());
            statement.setString(2, selfAssessment.getComments());
            statement.setDouble(3, selfAssessment.getGrade());
            statement.setString(4, selfAssessment.getRegistration());
            statement.setString(5, selfAssessment.getEvidenceId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateSelfAssessment(SelfAssessmentDTO selfAssessment, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, selfAssessment.getComments());
            statement.setDouble(2, selfAssessment.getGrade());
            statement.setString(3, selfAssessment.getRegistration());
            statement.setString(4, selfAssessment.getEvidenceId());
            statement.setString(5, selfAssessment.getSelfAssessmentId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteSelfAssessment(SelfAssessmentDTO selfAssessment, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, selfAssessment.getSelfAssessmentId());
            return statement.executeUpdate() > 0;
        }
    }

    public SelfAssessmentDTO getSelfAssessment(String selfAssessmentId, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, selfAssessmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new SelfAssessmentDTO(
                        resultSet.getString("idAutoevaluacion"),
                        resultSet.getString("comentarios"),
                        resultSet.getDouble("calificacion"),
                        resultSet.getString("matricula"),
                        resultSet.getString("idEvidencia")
                    );
                }
            }
        }
        return null;
    }

    public List<SelfAssessmentDTO> getAllSelfAssessments(Connection connection) throws SQLException {
        List<SelfAssessmentDTO> selfAssessments = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                selfAssessments.add(new SelfAssessmentDTO(
                    resultSet.getString("idAutoevaluacion"),
                    resultSet.getString("comentarios"),
                    resultSet.getDouble("calificacion"),
                    resultSet.getString("matricula"),
                    resultSet.getString("idEvidencia")
                ));
            }
        }
        return selfAssessments;
    }
}
