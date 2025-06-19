package logic.DAO;

import data_access.ConnectionDataBase;
import logic.DTO.EvaluationPresentationDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EvaluationPresentationDAO {

    private static final String SQL_INSERT = "INSERT INTO evaluacion_presentacion (idPresentacion, matricula, fecha, comentario, promedio) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE evaluacion_presentacion SET idPresentacion = ?, matricula = ?, fecha = ?, comentario = ?, promedio = ? WHERE idEvaluacion = ?";
    private static final String SQL_DELETE = "DELETE FROM evaluacion_presentacion WHERE idEvaluacion = ?";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM evaluacion_presentacion WHERE idEvaluacion = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM evaluacion_presentacion";
    private static final String SQL_SELECT_BY_TUITON = "SELECT * FROM evaluacion_presentacion WHERE matricula = ?";
    private static final String SQL_SELECT_BY_DATE = "SELECT * FROM evaluacion_presentacion WHERE fecha = ?";

    public int insertEvaluationPresentation(EvaluationPresentationDTO evaluation) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, evaluation.getIdProject());
            statement.setString(2, evaluation.getTuition());
            statement.setDate(3, new java.sql.Date(evaluation.getDate().getTime()));
            statement.setString(4, evaluation.getComment());
            statement.setDouble(5, evaluation.getAverage());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID generado para evaluacion_presentacion.");
                }
            }
        }
    }

    public boolean updateEvaluationPresentation(EvaluationPresentationDTO evaluation) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setInt(1, evaluation.getIdProject());
            statement.setString(2, evaluation.getTuition());
            statement.setDate(3, new java.sql.Date(evaluation.getDate().getTime()));
            statement.setString(4, evaluation.getComment());
            statement.setDouble(5, evaluation.getAverage());
            statement.setInt(6, evaluation.getIdEvaluation());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteEvaluationPresentation(int idEvaluation) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setInt(1, idEvaluation);
            return statement.executeUpdate() > 0;
        }
    }

    public EvaluationPresentationDTO searchEvaluationPresentationById(int idEvaluation) throws SQLException {
        EvaluationPresentationDTO evaluation = null;
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            statement.setInt(1, idEvaluation);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    evaluation = new EvaluationPresentationDTO(
                            resultSet.getInt("idEvaluacion"),
                            resultSet.getInt("idPresentacion"),
                            resultSet.getString("matricula"),
                            resultSet.getDate("fecha"),
                            resultSet.getString("comentario"),
                            resultSet.getDouble("promedio")
                    );
                }
            }
        }
        return evaluation;
    }

    public List<EvaluationPresentationDTO> getAllEvaluationPresentations() throws SQLException {
        List<EvaluationPresentationDTO> evaluations = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                evaluations.add(new EvaluationPresentationDTO(
                        resultSet.getInt("idEvaluacion"),
                        resultSet.getInt("idPresentacion"),
                        resultSet.getString("matricula"),
                        resultSet.getDate("fecha"),
                        resultSet.getString("comentario"),
                        resultSet.getDouble("promedio")
                ));
            }
        }
        return evaluations;
    }

    public int getLastInsertedId() throws SQLException {
        String SQL_LAST_INSERT_ID = "SELECT LAST_INSERT_ID()";
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_LAST_INSERT_ID);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        throw new SQLException("No se pudo obtener el último ID insertado.");
    }

    public List<EvaluationPresentationDTO> getEvaluationPresentationsByTuiton(String tuiton) throws SQLException {
        List<EvaluationPresentationDTO> evaluations = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_TUITON)) {
            statement.setString(1, tuiton);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    evaluations.add(new EvaluationPresentationDTO(
                            resultSet.getInt("idEvaluacion"),
                            resultSet.getInt("idPresentacion"),
                            resultSet.getString("matricula"),
                            resultSet.getDate("fecha"),
                            resultSet.getString("comentario"),
                            resultSet.getDouble("promedio")
                    ));
                }
            }
        }
        return evaluations;
    }

    public List<EvaluationPresentationDTO> getEvaluationPresentationsByDate(java.sql.Date date) throws SQLException {
        List<EvaluationPresentationDTO> evaluations = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_DATE)) {
            statement.setDate(1, date);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    evaluations.add(new EvaluationPresentationDTO(
                            resultSet.getInt("idEvaluacion"),
                            resultSet.getInt("idPresentacion"),
                            resultSet.getString("matricula"),
                            resultSet.getDate("fecha"),
                            resultSet.getString("comentario"),
                            resultSet.getDouble("promedio")
                    ));
                }
            }
        }
        return evaluations;
    }
}