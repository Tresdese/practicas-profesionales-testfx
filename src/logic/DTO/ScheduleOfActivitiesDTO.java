package logic.DTO;

import java.sql.Timestamp;

public class ScheduleOfActivitiesDTO {
    private String idSchedule;
    private String milestone;
    private Timestamp estimatedDate;
    private String tuiton;
    private String idEvidence;

    public ScheduleOfActivitiesDTO() {
        this.idSchedule = "";
        this.milestone = "";
        this.estimatedDate = null;
        this.tuiton = "";
        this.idEvidence = "";
    }

    public ScheduleOfActivitiesDTO(String idSchedule, String milestone, Timestamp estimatedDate, String tuiton, String idEvidence) {
        this.idSchedule = idSchedule;
        this.milestone = milestone;
        this.estimatedDate = estimatedDate;
        this.tuiton = tuiton;
        this.idEvidence = idEvidence;
    }

    public String getIdSchedule() {
        return idSchedule;
    }

    public void setIdSchedule(String idSchedule) {
        this.idSchedule = idSchedule;
    }

    public String getMilestone() {
        return milestone;
    }

    public void setMilestone(String milestone) {
        this.milestone = milestone;
    }

    public Timestamp getEstimatedDate() {
        return estimatedDate;
    }

    public void setEstimatedDate(Timestamp estimatedDate) {
        this.estimatedDate = estimatedDate;
    }

    public String getTuiton() {
        return tuiton;
    }

    public void setTuiton(String tuiton) {
        this.tuiton = tuiton;
    }

    public String getIdEvidence() {
        return idEvidence;
    }

    public void setIdEvidence(String idEvidence) {
        this.idEvidence = idEvidence;
    }

    @Override
    public String toString() {
        return "ScheduleOfActivitiesDTO{" +
                "idSchedule='" + idSchedule + '\'' +
                ", milestone='" + milestone + '\'' +
                ", estimatedDate=" + estimatedDate +
                ", tuiton='" + tuiton + '\'' +
                ", idEvidence='" + idEvidence + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ScheduleOfActivitiesDTO that = (ScheduleOfActivitiesDTO) obj;

        if (!idSchedule.equals(that.idSchedule)) return false;
        if (!milestone.equals(that.milestone)) return false;
        if (!estimatedDate.equals(that.estimatedDate)) return false;
        if (!tuiton.equals(that.tuiton)) return false;
        return idEvidence.equals(that.idEvidence);
    }
}
