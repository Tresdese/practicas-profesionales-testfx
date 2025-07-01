package logic.DAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import data_access.ConnectionDataBase;
import logic.DTO.SelfAssessmentCriteriaDTO;
import logic.interfaces.ISelfAssessmentCriterialDAO;

public class SelfAssessmentCriteriaDAO implements ISelfAssessmentCriterialDAO {
    private final static String SQL_INSERT = "INSERT INTO criterio_de_autoevaluacion (idCriterios, nombreCriterio) VALUES (?, ?)";
    private final static String SQL_UPDATE = "UPDATE criterio_de_autoevaluacion SET nombreCriterio = ? WHERE idCriterios = ?";
    private final static String SQL_DELETE = "DELETE FROM criterio_de_autoevaluacion WHERE idCriterios = ?";
    private final static String SQL_SELECT = "SELECT * FROM criterio_de_autoevaluacion WHERE idCriterios = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM criterio_de_autoevaluacion";

    public boolean insertSelfAssessmentCriteria(SelfAssessmentCriteriaDTO criteria) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, criteria.getIdCriteria());
            statement.setString(2, criteria.getNameCriteria());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateSelfAssessmentCriteria(SelfAssessmentCriteriaDTO criteria) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, criteria.getNameCriteria());
            statement.setString(2, criteria.getIdCriteria());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteSelfAssessmentCriteria(SelfAssessmentCriteriaDTO criteria) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, criteria.getIdCriteria());
            return statement.executeUpdate() > 0;
        }
    }

    public SelfAssessmentCriteriaDTO searchSelfAssessmentCriteriaById(String idCriteria) throws SQLException, IOException {
        SelfAssessmentCriteriaDTO selfAssessmentCriteria = new SelfAssessmentCriteriaDTO(
                "N/A",
                "N/A"
        );
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, idCriteria);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    selfAssessmentCriteria = new SelfAssessmentCriteriaDTO(
                            resultSet.getString("idCriterios"),
                            resultSet.getString("nombreCriterio")
                    );
                }
            }
        }
        return selfAssessmentCriteria;
    }

    public List<SelfAssessmentCriteriaDTO> getAllSelfAssessmentCriteria() throws SQLException, IOException {
        List<SelfAssessmentCriteriaDTO> criteriaList = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                criteriaList.add(new SelfAssessmentCriteriaDTO(
                        resultSet.getString("idCriterios"),
                        resultSet.getString("nombreCriterio")
                ));
            }
        }
        return criteriaList;
    }
}

