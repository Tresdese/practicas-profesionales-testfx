package logic.DTO;

import java.util.Date;

public class ReportDTO {
    private String numberReport;
    private Date reportDate;
    private int totalHours;
    private String generalObjective;
    private String methodology;
    private String obtainedResult;
    private int projectId;
    private String tuition;
    private String observations;
    private String idEvidence;

    public ReportDTO() {
        this.numberReport = "";
        this.reportDate = null;
        this.totalHours = 0;
        this.generalObjective = "";
        this.methodology = "";
        this.obtainedResult = "";
        this.projectId = 0;
        this.tuition = "";
        this.observations = "";
        this.idEvidence = "";
    }

    public ReportDTO(String numberReport, Date reportDate, int totalHours, String generalObjective, String methodology,
                     String obtainedResult, int projectId, String tuition, String observations, String idEvidence) {
        this.numberReport = numberReport;
        this.reportDate = reportDate;
        this.totalHours = totalHours;
        this.generalObjective = generalObjective;
        this.methodology = methodology;
        this.obtainedResult = obtainedResult;
        this.projectId = projectId;
        this.tuition = tuition;
        this.observations = observations;
        this.idEvidence = idEvidence;
    }

    public String getNumberReport() { return numberReport; }
    public void setNumberReport(String numberReport) { this.numberReport = numberReport; }

    public Date getReportDate() { return reportDate; }
    public void setReportDate(Date reportDate) { this.reportDate = reportDate; }

    public int getTotalHours() { return totalHours; }
    public void setTotalHours(int totalHours) { this.totalHours = totalHours; }

    public String getGeneralObjective() { return generalObjective; }
    public void setGeneralObjective(String generalObjective) { this.generalObjective = generalObjective; }

    public String getMethodology() { return methodology; }
    public void setMethodology(String methodology) { this.methodology = methodology; }

    public String getObtainedResult() { return obtainedResult; }
    public void setObtainedResult(String obtainedResult) { this.obtainedResult = obtainedResult; }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public String getTuition() { return tuition; }
    public void setTuition(String tuition) { this.tuition = tuition; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public String getIdEvidence() { return idEvidence; }
    public void setIdEvidence(String idEvidence) { this.idEvidence = idEvidence; }

    @Override
    public String toString() {
        return "ReportDTO{" +
                "numberReport='" + numberReport + '\'' +
                ", reportDate=" + reportDate +
                ", totalHours=" + totalHours +
                ", generalObjective='" + generalObjective + '\'' +
                ", methodology='" + methodology + '\'' +
                ", obtainedResult='" + obtainedResult + '\'' +
                ", projectId=" + projectId +
                ", tuition='" + tuition + '\'' +
                ", observations='" + observations + '\'' +
                ", idEvidence='" + idEvidence + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ReportDTO that = (ReportDTO) obj;

        if (!numberReport.equals(that.numberReport)) return false;
        if (reportDate != null ? !reportDate.equals(that.reportDate) : that.reportDate != null) return false;
        if (totalHours != that.totalHours) return false;
        if (!generalObjective.equals(that.generalObjective)) return false;
        if (!methodology.equals(that.methodology)) return false;
        if (!obtainedResult.equals(that.obtainedResult)) return false;
        if (projectId != that.projectId) return false;
        if (!tuition.equals(that.tuition)) return false;
        if (!observations.equals(that.observations)) return false;
        return idEvidence.equals(that.idEvidence);
    }
}