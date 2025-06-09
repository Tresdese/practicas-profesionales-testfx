package logic.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.PeriodDTO;

public interface IPeriodDAO {
    boolean insertPeriod(PeriodDTO period) throws SQLException;

    boolean updatePeriod(PeriodDTO period) throws SQLException;

    boolean deletePeriodById(String idPeriod) throws SQLException;

    PeriodDTO searchPeriodById(String idPeriod) throws SQLException;

    List<PeriodDTO> getAllPeriods() throws SQLException;
}
