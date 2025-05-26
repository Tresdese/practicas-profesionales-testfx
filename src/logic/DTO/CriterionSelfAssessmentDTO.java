package logic.DTO;

public class CriterionSelfAssessmentDTO {
    private int idSelfAssessment;
    private int idCriteria;
    private float grade;
    private String comments;

    public CriterionSelfAssessmentDTO() {
        this.idSelfAssessment = 0;
        this.idCriteria = 0;
        this.grade = 0.0f;
        this.comments = "";
    }

    public CriterionSelfAssessmentDTO(int idSelfAssessment, int idCriteria, float grade, String comments) {
        this.idSelfAssessment = idSelfAssessment;
        this.idCriteria = idCriteria;
        this.grade = grade;
        this.comments = comments;
    }

    public int getIdSelfAssessment() {
        return idSelfAssessment;
    }

    public void setIdSelfAssessment(int idSelfAssessment) {
        this.idSelfAssessment = idSelfAssessment;
    }

    public int getIdCriteria() {
        return idCriteria;
    }

    public void setIdCriteria(int idCriteria) {
        this.idCriteria = idCriteria;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}