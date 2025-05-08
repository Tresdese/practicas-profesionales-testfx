package logic.DAO;

import data_access.ConecctionDataBase;
import logic.DTO.EvaluationDetailDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EvaluationDetailDAO {

    private static final String SQL_INSERT = "INSERT INTO detalle_evaluacion (idDetalle, idEvaluacion, idCriterio, calificacion) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE detalle_evaluacion SET idEvaluacion = ?, idCriterio = ?, calificacion = ? WHERE idDetalle = ?";
    private static final String SQL_DELETE = "DELETE FROM detalle_evaluacion WHERE idDetalle = ?";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM detalle_evaluacion WHERE idDetalle = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM detalle_evaluacion";

    public boolean insertEvaluationDetail(EvaluationDetailDTO detail) throws SQLException {
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setInt(1, detail.getIdDetail());
            statement.setInt(2, detail.getIdEvaluation());
            statement.setInt(3, detail.getIdCriteria());
            statement.setDouble(4, detail.getGrade());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateEvaluationDetail(EvaluationDetailDTO detail) throws SQLException {
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setInt(1, detail.getIdEvaluation());
            statement.setInt(2, detail.getIdCriteria());
            statement.setDouble(3, detail.getGrade());
            statement.setInt(4, detail.getIdDetail());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteEvaluationDetail(int idDetail) throws SQLException {
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setInt(1, idDetail);
            return statement.executeUpdate() > 0;
        }
    }

    public EvaluationDetailDTO searchEvaluationDetailById(int idDetail) throws SQLException {
        EvaluationDetailDTO detail = null;
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            statement.setInt(1, idDetail);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    detail = new EvaluationDetailDTO(
                            resultSet.getInt("idDetalle"),
                            resultSet.getInt("idEvaluacion"),
                            resultSet.getInt("idCriterio"),
                            resultSet.getDouble("calificacion")
                    );
                }
            }
        }
        return detail;
    }

    public List<EvaluationDetailDTO> getAllEvaluationDetails() throws SQLException {
        List<EvaluationDetailDTO> details = new ArrayList<>();
        try (ConecctionDataBase connectionDataBase = new ConecctionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                details.add(new EvaluationDetailDTO(
                        resultSet.getInt("idDetalle"),
                        resultSet.getInt("idEvaluacion"),
                        resultSet.getInt("idCriterio"),
                        resultSet.getDouble("calificacion")
                ));
            }
        }
        return details;
    }
}