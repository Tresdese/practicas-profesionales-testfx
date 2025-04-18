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

    @Override
    public String toString() {
        return "ActivityReportDTO{" +
                "numberReport='" + numberReport + '\'' +
                ", idActivity='" + idActivity + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ActivityReportDTO that = (ActivityReportDTO) obj;

        if (!numberReport.equals(that.numberReport)) return false;
        return idActivity.equals(that.idActivity);
    }
}
