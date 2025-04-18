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

    @Override
    public String toString() {
        return "AssessmentCriterionDTO{" +
                "idCriterion='" + idCriterion + '\'' +
                ", nameCriterion='" + nameCriterion + '\'' +
                ", grade=" + grade +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        AssessmentCriterionDTO that = (AssessmentCriterionDTO) obj;

        if (Double.compare(that.grade, grade) != 0) return false;
        if (!idCriterion.equals(that.idCriterion)) return false;
        return nameCriterion.equals(that.nameCriterion);
    }
}
