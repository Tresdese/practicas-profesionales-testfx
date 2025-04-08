package logic.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.PeriodDTO;

public interface IPeriodDAO {
    boolean insertPeriod(PeriodDTO period, Connection connection) throws SQLException;

    boolean updatePeriod(PeriodDTO period, Connection connection) throws SQLException;

    boolean deletePeriodById(String idPeriod, Connection connection) throws SQLException;

    PeriodDTO getPeriod(String idPeriod, Connection connection) throws SQLException;

    List<PeriodDTO> getAllPeriods(Connection connection) throws SQLException;
}
