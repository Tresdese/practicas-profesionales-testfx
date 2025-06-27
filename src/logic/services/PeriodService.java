package logic.services;

import logic.DAO.PeriodDAO;
import logic.DTO.PeriodDTO;
import logic.exceptions.RepeatedId;

public class PeriodService {

    private final PeriodDAO periodDAO;

    public PeriodService() {
        this.periodDAO = new PeriodDAO();
    }

    public boolean registerPeriod(PeriodDTO period) throws RepeatedId, Exception {

        if (periodDAO.isIdRegistered(period.getIdPeriod())) {
            throw new RepeatedId("El ID del periodo ya está registrado.");
        }
        if (!periodDAO.insertPeriod(period)) {
            throw new Exception("No se pudo registrar el periodo.");
        }

        return periodDAO.insertPeriod(period);
    }

    public void updatePeriod(PeriodDTO period) throws Exception {
        boolean success = periodDAO.updatePeriod(period);
        if (!success) {
            throw new Exception("No se pudo actualizar el periodo.");
        }
    }

    public PeriodDTO searchPeriodById(String id) throws Exception {
        return periodDAO.searchPeriodById(id);
    }

}
