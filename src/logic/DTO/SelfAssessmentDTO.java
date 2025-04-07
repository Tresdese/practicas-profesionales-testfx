package logic.DTO;

public class SelfAssessmentDTO {
    private String selfAssessmentId;
    private String comments;
    private double grade;
    private String registration;
    private String evidenceId;

    public SelfAssessmentDTO() {
        this.selfAssessmentId = "";
        this.comments = "";
        this.grade = 0;
        this.registration = "";
        this.evidenceId = "";
    }

    public SelfAssessmentDTO(String selfAssessmentId, String comments, double grade, String registration, String evidenceId) {
        this.selfAssessmentId = selfAssessmentId;
        this.comments = comments;
        this.grade = grade;
        this.registration = registration;
        this.evidenceId = evidenceId;
    }

    public String getSelfAssessmentId() {
        return selfAssessmentId;
    }

    public void setSelfAssessmentId(String selfAssessmentId) {
        this.selfAssessmentId = selfAssessmentId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getEvidenceId() {
        return evidenceId;
    }

    public void setEvidenceId(String evidenceId) {
        this.evidenceId = evidenceId;
    }
}