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

    @Override
    public String toString() {
        return "ActivityScheduleDTO{" +
                "idSchedule='" + idSchedule + '\'' +
                ", idActivity='" + idActivity + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ActivityScheduleDTO that = (ActivityScheduleDTO) obj;

        if (!idSchedule.equals(that.idSchedule)) return false;
        return idActivity.equals(that.idActivity);
    }
}
