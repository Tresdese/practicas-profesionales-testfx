package logic.DAO;

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
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
            ps.setString(1, period.getIdPeriod());
            ps.setString(2, period.getName());
            ps.setTimestamp(3, period.getStartDate());
            ps.setTimestamp(4, period.getEndDate());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePeriod(PeriodDTO period, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, period.getName());
            ps.setTimestamp(2, period.getStartDate());
            ps.setTimestamp(3, period.getEndDate());
            ps.setString(4, period.getIdPeriod());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deletePeriodById(String idPeriod, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setString(1, idPeriod);
            return ps.executeUpdate() > 0;
        }
    }

    public PeriodDTO getPeriod(String idPeriod, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ps.setString(1, idPeriod);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PeriodDTO(rs.getString("idPeriodo"), rs.getString("nombre"), rs.getTimestamp("fechaInicio"), rs.getTimestamp("fechaFin"));
                }
            }
        }
        return null;
    }

    public List<PeriodDTO> getAllPeriods(Connection connection) throws SQLException {
        List<PeriodDTO> periods = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                periods.add(new PeriodDTO(rs.getString("idPeriodo"), rs.getString("nombre"), rs.getTimestamp("fechaInicio"), rs.getTimestamp("fechaFin")));
            }
        }
        return periods;
    }
}
