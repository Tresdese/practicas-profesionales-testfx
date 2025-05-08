package logic.DTO;

public class AssessmentCriterionDTO {
    private String idCriterion;
    private String nameCriterion;

    public AssessmentCriterionDTO() {
        this.idCriterion = "";
        this.nameCriterion = "";
    }

    public AssessmentCriterionDTO(String idCriterion, String nameCriterion) {
        this.idCriterion = idCriterion;
        this.nameCriterion = nameCriterion;
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

    @Override
    public String toString() {
        return "AssessmentCriterionDTO{" +
                "idCriterion='" + idCriterion + '\'' +
                ", nameCriterion='" + nameCriterion + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        AssessmentCriterionDTO that = (AssessmentCriterionDTO) obj;

        if (!idCriterion.equals(that.idCriterion)) return false;
        return nameCriterion.equals(that.nameCriterion);
    }
}
