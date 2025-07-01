package logic.DAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import data_access.ConnectionDataBase;
import logic.DTO.PeriodDTO;
import logic.interfaces.IPeriodDAO;

public class PeriodDAO implements IPeriodDAO {
    private final static String SQL_INSERT = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE periodo SET nombre = ?, fechaInicio = ?, fechaFin = ? WHERE idPeriodo = ?";
    private final static String SQL_DELETE = "DELETE FROM periodo WHERE idPeriodo = ?";
    private final static String SQL_SELECT = "SELECT * FROM periodo WHERE idPeriodo = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM periodo";
    private static final String SQL_COUNT_BY_ID = "SELECT COUNT(*) FROM periodo WHERE idPeriodo = ?";

    @Override
    public boolean insertPeriod(PeriodDTO period) throws SQLException, IOException {
        PeriodDTO existingPeriod = searchPeriodById(period.getIdPeriod());
        if (existingPeriod != null && !"N/A".equals(existingPeriod.getIdPeriod())) {
            return false;
        }
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, period.getIdPeriod());
            statement.setString(2, period.getName());
            statement.setTimestamp(3, period.getStartDate());
            statement.setTimestamp(4, period.getEndDate());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updatePeriod(PeriodDTO period) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, period.getName());
            statement.setTimestamp(2, period.getStartDate());
            statement.setTimestamp(3, period.getEndDate());
            statement.setString(4, period.getIdPeriod());
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deletePeriodById(String idPeriod) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, idPeriod);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public PeriodDTO searchPeriodById(String idPeriod) throws SQLException, IOException {
        PeriodDTO period = new PeriodDTO("N/A", "N/A", java.sql.Timestamp.valueOf("0404-01-01 00:00:00"), java.sql.Timestamp.valueOf("0404-01-01 00:00:00"));
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, idPeriod);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    period = new PeriodDTO(resultSet.getString("idPeriodo"), resultSet.getString("nombre"), resultSet.getTimestamp("fechaInicio"), resultSet.getTimestamp("fechaFin"));
                }
            }
        }
        return period;
    }

    @Override
    public List<PeriodDTO> getAllPeriods() throws SQLException, IOException {
        List<PeriodDTO> periods = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                periods.add(new PeriodDTO(resultSet.getString("idPeriodo"), resultSet.getString("nombre"), resultSet.getTimestamp("fechaInicio"), resultSet.getTimestamp("fechaFin")));
            }
        }
        return periods;
    }

    @Override
    public boolean isIdRegistered(String id) throws SQLException, IOException {
        boolean success = false;
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_COUNT_BY_ID)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    success = resultSet.getInt(1) > 0;
                }
            }
        }

        return success;
    }
}