package logic.DTO;

import java.sql.Timestamp;

public class PeriodDTO {
    private String idPeriod;
    private String name;
    private Timestamp startDate;
    private Timestamp endDate;

    public PeriodDTO() {
        this.idPeriod = "";
        this.name = "";
        this.startDate = null;
        this.endDate = null;
    }

    public PeriodDTO(String idPeriod, String name, Timestamp startDate, Timestamp endDate) {
        this.idPeriod = idPeriod;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getIdPeriod() {
        return idPeriod;
    }

    public void setIdPeriod(String idPeriod) {
        this.idPeriod = idPeriod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }
}
