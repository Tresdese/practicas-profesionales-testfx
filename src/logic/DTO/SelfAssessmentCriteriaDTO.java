package logic.DTO;

public class SelfAssessmentCriteriaDTO {
    private String idCriteria;
    private String nameCriteria;
    private double grade;

    public SelfAssessmentCriteriaDTO() {
        this.idCriteria = "";
        this.nameCriteria = "";
        this.grade = 0;
    }

    public SelfAssessmentCriteriaDTO(String idCriteria, String nameCriteria, double grade) {
        this.idCriteria = idCriteria;
        this.nameCriteria = nameCriteria;
        this.grade = grade;
    }

    public String getIdCriteria() {
        return idCriteria;
    }

    public void setIdCriteria(String idCriteria) {
        this.idCriteria = idCriteria;
    }

    public String getNameCriteria() {
        return nameCriteria;
    }

    public void setNameCriteria(String nameCriteria) {
        this.nameCriteria = nameCriteria;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "SelfAssessmentCriteriaDTO{" +
                "idCriteria='" + idCriteria + '\'' +
                ", nameCriteria='" + nameCriteria + '\'' +
                ", grade=" + grade +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        SelfAssessmentCriteriaDTO that = (SelfAssessmentCriteriaDTO) obj;

        if (Double.compare(that.grade, grade) != 0) return false;
        if (!idCriteria.equals(that.idCriteria)) return false;
        return nameCriteria.equals(that.nameCriteria);
    }
}
