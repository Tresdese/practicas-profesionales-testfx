package logic.DTO;

public class ActivityScheduleDTO {
    private String idSchedule;
    private String idActivity;

    public ActivityScheduleDTO() {
        this.idSchedule = "";
        this.idActivity = "";
    }

    public ActivityScheduleDTO(String idSchedule, String idActivity) {
        this.idSchedule = idSchedule;
        this.idActivity = idActivity;
    }

    public String getIdSchedule() {
        return idSchedule;
    }

    public void setIdSchedule(String idSchedule) {
        this.idSchedule = idSchedule;
    }

    public String getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(String idActivity) {
        this.idActivity = idActivity;
    }
}
