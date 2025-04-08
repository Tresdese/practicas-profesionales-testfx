package data_access.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.PartialEvaluationDTO;

public class PartialEvaluationDAO {
    private final static String SQL_INSERT = "INSERT INTO evaluacion_parcial (idEvaluacion, promedio, matricula, IdEvidencia) VALUES (?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE evaluacion_parcial SET promedio = ?, matricula = ?, IdEvidencia = ? WHERE idEvaluacion = ?";
    private final static String SQL_DELETE = "DELETE FROM evaluacion_parcial WHERE idEvaluacion = ?";
    private final static String SQL_SELECT = "SELECT * FROM evaluacion_parcial WHERE idEvaluacion = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM evaluacion_parcial";

    public boolean insertPartialEvaluation(PartialEvaluationDTO evaluation, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, evaluation.getIdEvaluation());
            statement.setDouble(2, evaluation.getAverage());
            statement.setString(3, evaluation.getTuiton());
            statement.setString(4, evaluation.getEvidence());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updatePartialEvaluation(PartialEvaluationDTO evaluation, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setDouble(1, evaluation.getAverage());
            statement.setString(2, evaluation.getTuiton());
            statement.setString(3, evaluation.getEvidence());
            statement.setString(4, evaluation.getIdEvaluation());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deletePartialEvaluation(String idEvaluation, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, idEvaluation);
            return statement.executeUpdate() > 0;
        }
    }

    public PartialEvaluationDTO getPartialEvaluation(String idEvaluation, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, idEvaluation);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new PartialEvaluationDTO(resultSet.getString("idEvaluacion"), resultSet.getDouble("promedio"), resultSet.getString("matricula"), resultSet.getString("IdEvidencia"));
                }
            }
        }
        return null;
    }

    public List<PartialEvaluationDTO> getAllPartialEvaluations(Connection connection) throws SQLException {
        List<PartialEvaluationDTO> evaluations = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                evaluations.add(new PartialEvaluationDTO(resultSet.getString("idEvaluacion"), resultSet.getDouble("promedio"), resultSet.getString("matricula"), resultSet.getString("IdEvidencia")));
            }
        }
        return evaluations;
    }
}
