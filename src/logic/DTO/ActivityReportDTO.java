package logic.DTO;

public class ActivityReportDTO {
    private String numberReport;
    private String idActivity;

    public ActivityReportDTO() {
        this.numberReport = "";
        this.idActivity = "";
    }

    public ActivityReportDTO(String numberReport, String idActivity) {
        this.numberReport = numberReport;
        this.idActivity = idActivity;
    }

    public String getNumberReport() {
        return numberReport;
    }

    public void setNumberReport(String numberReport) {
        this.numberReport = numberReport;
    }

    public String getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(String idActivity) {
        this.idActivity = idActivity;
    }
}
