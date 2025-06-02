package logic.DTO;

public class ActivityReportDTO {
    private String numberReport;
    private String idActivity;
    private int progressPercentage;
    private String observations;

    public ActivityReportDTO() {
        this.numberReport = "";
        this.idActivity = "";
        this.progressPercentage = 0;
        this.observations = "";
    }

    public ActivityReportDTO(String numberReport, String idActivity, int progressPercentage, String observations) {
        this.numberReport = numberReport;
        this.idActivity = idActivity;
        this.progressPercentage = progressPercentage;
        this.observations = observations;
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

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    @Override
    public String toString() {
        return "ActivityReportDTO{" +
                "numberReport='" + numberReport + '\'' +
                ", idActivity='" + idActivity + '\'' +
                ", progressPercentage=" + progressPercentage +
                ", observations='" + observations + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ActivityReportDTO that = (ActivityReportDTO) obj;

        if (!numberReport.equals(that.numberReport)) return false;
        if (!idActivity.equals(that.idActivity)) return false;
        if (progressPercentage != that.progressPercentage) return false;
        return observations.equals(that.observations);
    }
}