package logic.DTO;

import java.math.BigDecimal;

public class UserStudentViewDTO {

    private String tuition;
    private int status;
    private String studentNames;
    private String studentSurnames;
    private String phoneNumber;
    private String email;
    private String studentUsername;
    private Integer creditProgress;
    private BigDecimal finalGrade;
    private int nrc;
    private String groupName;
    private int userId;
    private int staffNumber;
    private String userNames;
    private String userSurnames;
    private String username;
    private String role;

    public UserStudentViewDTO(String tuition, int status, String studentNames, String studentSurnames,
                              String phoneNumber, String email, String studentUsername, Integer creditProgress,
                              BigDecimal finalGrade, int nrc, String groupName, int userId,
                              int staffNumber, String userNames, String userSurnames,
                              String username, String role) {
        this.tuition = tuition;
        this.status = status;
        this.studentNames = studentNames;
        this.studentSurnames = studentSurnames;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.studentUsername = studentUsername;
        this.creditProgress = creditProgress;
        this.finalGrade = finalGrade;
        this.nrc = nrc;
        this.groupName = groupName;
        this.userId = userId;
        this.staffNumber = staffNumber;
        this.userNames = userNames;
        this.userSurnames = userSurnames;
        this.username = username;
        this.role = role;
    }

    public UserStudentViewDTO() {
        this.tuition = "";
        this.status = 1;
        this.studentNames = "";
        this.studentSurnames = "";
        this.phoneNumber = "";
        this.email = "";
        this.studentUsername = "";
        this.creditProgress = 0;
        this.finalGrade = BigDecimal.ZERO;
        this.nrc = 0;
        this.groupName = "";
        this.userId = 0;
        this.staffNumber = 0;
        this.userNames = "";
        this.userSurnames = "";
        this.username = "";
        this.role = "";
    }

    public String getTuition() {
        return tuition;
    }

    public void setTuition(String tuition) {
        this.tuition = tuition;
    }

    public int isStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStudentNames() {
        return studentNames;
    }

    public void setStudentNames(String studentNames) {
        this.studentNames = studentNames;
    }

    public String getStudentSurnames() {
        return studentSurnames;
    }

    public void setStudentSurnames(String studentSurnames) {
        this.studentSurnames = studentSurnames;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStudentUsername() {
        return studentUsername;
    }

    public void setStudentUsername(String studentUsername) {
        this.studentUsername = studentUsername;
    }

    public Integer getCreditProgress() {
        return creditProgress;
    }

    public void setCreditProgress(Integer creditProgress) {
        this.creditProgress = creditProgress;
    }

    public BigDecimal getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(BigDecimal finalGrade) {
        this.finalGrade = finalGrade;
    }

    public int getNrc() {
        return nrc;
    }

    public void setNrc(int nrc) {
        this.nrc = nrc;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(int staffNumber) {
        this.staffNumber = staffNumber;
    }

    public String getUserNames() {
        return userNames;
    }

    public void setUserNames(String userNames) {
        this.userNames = userNames;
    }

    public String getUserSurnames() {
        return userSurnames;
    }

    public void setUserSurnames(String userSurnames) {
        this.userSurnames = userSurnames;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserStudentViewDTO{" +
                "tuition='" + tuition + '\'' +
                ", status=" + status +
                ", studentNames='" + studentNames + '\'' +
                ", studentSurnames='" + studentSurnames + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", studentUsername='" + studentUsername + '\'' +
                ", creditProgress=" + creditProgress +
                ", finalGrade=" + finalGrade +
                ", nrc=" + nrc +
                ", groupName='" + groupName + '\'' +
                ", userId=" + userId +
                ", staffNumber=" + staffNumber +
                ", userNames='" + userNames + '\'' +
                ", userSurnames='" + userSurnames + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserStudentViewDTO)) return false;

        UserStudentViewDTO that = (UserStudentViewDTO) o;

        return tuition.equals(that.tuition);
    }
}