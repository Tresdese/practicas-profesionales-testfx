package data_access.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.PeriodDTO;

public class PeriodDAO {
    private final static String SQL_INSERT = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE periodo SET nombre = ?, fechaInicio = ?, fechaFin = ? WHERE idPeriodo = ?";
    private final static String SQL_DELETE = "DELETE FROM periodo WHERE idPeriodo = ?";
    private final static String SQL_SELECT = "SELECT * FROM periodo WHERE idPeriodo = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM periodo";

    public boolean insertPeriod(PeriodDTO period, Connection connection) throws SQLException {
        PeriodDTO existingPeriod = getPeriod(period.getIdPeriod(), connection);
        if (existingPeriod != null) {
            return period.getIdPeriod().equals(existingPeriod.getIdPeriod());
        }

        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, period.getIdPeriod());
            statement.setString(2, period.getName());
            statement.setTimestamp(3, period.getStartDate());
            statement.setTimestamp(4, period.getEndDate());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updatePeriod(PeriodDTO period, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, period.getName());
            statement.setTimestamp(2, period.getStartDate());
            statement.setTimestamp(3, period.getEndDate());
            statement.setString(4, period.getIdPeriod());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deletePeriod(String idPeriod, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, idPeriod);
            return statement.executeUpdate() > 0;
        }
    }

    public PeriodDTO getPeriod(String idPeriod, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, idPeriod);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new PeriodDTO(resultSet.getString("idPeriodo"), resultSet.getString("nombre"), resultSet.getTimestamp("fechaInicio"), resultSet.getTimestamp("fechaFin"));
                }
            }
        }
        return null;
    }

    public List<PeriodDTO> getAllPeriods(Connection connection) throws SQLException {
        List<PeriodDTO> periods = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                periods.add(new PeriodDTO(resultSet.getString("idPeriodo"), resultSet.getString("nombre"), resultSet.getTimestamp("fechaInicio"), resultSet.getTimestamp("fechaFin")));
            }
        }
        return periods;
    }
}
