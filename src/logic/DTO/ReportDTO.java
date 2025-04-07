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
}
