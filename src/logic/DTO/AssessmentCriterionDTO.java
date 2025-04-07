package logic.DTO;

public class AssessmentCriterionDTO {
    private String idCriterion;
    private String nameCriterion;
    private double grade;

    public AssessmentCriterionDTO() {
        this.idCriterion = "";
        this.nameCriterion = "";
        this.grade = 0;
    }

    public AssessmentCriterionDTO(String idCriterion, String nameCriterion, double grade) {
        this.idCriterion = idCriterion;
        this.nameCriterion = nameCriterion;
        this.grade = grade;
    }

    public String getIdCriterion() {
        return idCriterion;
    }

    public void setIdCriterion(String idCriterion) {
        this.idCriterion = idCriterion;
    }

    public String getNameCriterion() {
        return nameCriterion;
    }

    public void setNameCriterion(String nameCriterion) {
        this.nameCriterion = nameCriterion;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }
}
