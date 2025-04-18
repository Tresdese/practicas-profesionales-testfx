package logic.DTO;

public class ReportDTO {
    private String numberReport;
    private String observations;
    private String idEvidence;

    public ReportDTO() {
        this.numberReport = "";
        this.observations = "";
        this.idEvidence = "";
    }

    public ReportDTO(String numberReport, String observations, String idEvidence) {
        this.numberReport = numberReport;
        this.observations = observations;
        this.idEvidence = idEvidence;
    }

    public String getNumberReport() {
        return numberReport;
    }

    public void setNumberReport(String numberReport) {
        this.numberReport = numberReport;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getIdEvidence() {
        return idEvidence;
    }

    public void setIdEvidence(String idEvidence) {
        this.idEvidence = idEvidence;
    }

    @Override
    public String toString() {
        return "ReportDTO{" +
                "numberReport='" + numberReport + '\'' +
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
        if (!observations.equals(that.observations)) return false;
        return idEvidence.equals(that.idEvidence);
    }
}
