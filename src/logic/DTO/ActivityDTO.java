package logic.DTO;

public class ActivityDTO {
    private String activityId;
    private String activityName;

    public ActivityDTO() {
        this.activityId = "";
        this.activityName = "";
    }

    public ActivityDTO(String activityId, String activityName) {
        this.activityId = activityId;
        this.activityName = activityName;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    @Override
    public String toString() {
        return "ActivityDTO{" +
                "activityId='" + activityId + '\'' +
                ", activityName='" + activityName + '\'' +
                '}';
    }
}