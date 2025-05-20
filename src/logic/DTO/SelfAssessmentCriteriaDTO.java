package logic.DTO;

public class SelfAssessmentCriteriaDTO {
    private String idCriteria;
    private String nameCriteria;

    public SelfAssessmentCriteriaDTO() {
        this.idCriteria = "";
        this.nameCriteria = "";
    }

    public SelfAssessmentCriteriaDTO(String idCriteria, String nameCriteria) {
        this.idCriteria = idCriteria;
        this.nameCriteria = nameCriteria;
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

    @Override
    public String toString() {
        return "SelfAssessmentCriteriaDTO{" +
                "idCriteria='" + idCriteria + '\'' +
                ", nameCriteria='" + nameCriteria + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        SelfAssessmentCriteriaDTO that = (SelfAssessmentCriteriaDTO) obj;

        if (!idCriteria.equals(that.idCriteria)) return false;
        return nameCriteria.equals(that.nameCriteria);
    }
}
