package logic.DTO;

import java.util.Date;

public class ProjectStudentViewDTO {
    private int projectId;
    private String projectName;
    private String description;
    private Date estimatedDate;
    private Date startDate;
    private String tuition;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String username;
    private Integer NRC;
    private Integer creditProgress;

    public ProjectStudentViewDTO(int projectId, String projectName, String description, Date estimatedDate, Date startDate, String tuiton, String firstName, String lastName, String phone, String email, String username, Integer NRC, Integer creditProgress) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.description = description;
        this.estimatedDate = estimatedDate;
        this.startDate = startDate;
        this.tuition = tuiton;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.NRC = NRC;
        this.creditProgress = creditProgress;
    }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getEstimatedDate() { return estimatedDate; }
    public void setEstimatedDate(Date estimatedDate) { this.estimatedDate = estimatedDate; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public String getEnrollment() { return tuition; }
    public void setEnrollment(String tuiton) { this.tuition = tuiton; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Integer getNrc() { return NRC; }
    public void setNrc(Integer NRC) { this.NRC = NRC; }

    public Integer getCreditProgress() { return creditProgress; }
    public void setCreditProgress(Integer creditProgress) { this.creditProgress = creditProgress; }
}