package logic.DTO;

public class ActivityScheduleDTO {
    private int idSchedule;
    private int idActivity;

    public ActivityScheduleDTO() {
        this.idSchedule = 0;
        this.idActivity = 0;
    }

    public ActivityScheduleDTO(int idSchedule, int idActivity) {
        this.idSchedule = idSchedule;
        this.idActivity = idActivity;
    }

    public int getIdSchedule() {
        return idSchedule;
    }

    public void setIdSchedule(int idSchedule) {
        this.idSchedule = idSchedule;
    }

    public int getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(int idActivity) {
        this.idActivity = idActivity;
    }

    @Override
    public String toString() {
        return "ActivityScheduleDTO{" +
                "idSchedule=" + idSchedule +
                ", idActivity=" + idActivity +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ActivityScheduleDTO that = (ActivityScheduleDTO) obj;

        if (idSchedule != that.idSchedule) return false;
        return idActivity == that.idActivity;
    }
}
