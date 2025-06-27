package logic.interfaces;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.PeriodDTO;

public interface IPeriodDAO {
    boolean insertPeriod(PeriodDTO period) throws SQLException, IOException;

    boolean updatePeriod(PeriodDTO period) throws SQLException, IOException;

    boolean deletePeriodById(String idPeriod) throws SQLException, IOException;

    PeriodDTO searchPeriodById(String idPeriod) throws SQLException, IOException;

    List<PeriodDTO> getAllPeriods() throws SQLException, IOException;

    boolean isIdRegistered(String id) throws SQLException, IOException;
}
