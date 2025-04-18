package logic.DTO;

public class CriterionSelfAssessmentDTO {
    private String idSelfAssessment;
    private String idCriteria;

    public CriterionSelfAssessmentDTO() {
        this.idSelfAssessment = "";
        this.idCriteria = "";
    }

    public CriterionSelfAssessmentDTO(String idSelfAssessment, String idCriteria) {
        this.idSelfAssessment = idSelfAssessment;
        this.idCriteria = idCriteria;
    }

    public String getIdSelfAssessment() {
        return idSelfAssessment;
    }

    public void setIdSelfAssessment(String idSelfAssessment) {
        this.idSelfAssessment = idSelfAssessment;
    }

    public String getIdCriteria() {
        return idCriteria;
    }

    public void setIdCriteria(String idCriteria) {
        this.idCriteria = idCriteria;
    }

    @Override
    public String toString() {
        return "CriterionSelfAssessmentDTO{" +
                "idSelfAssessment='" + idSelfAssessment + '\'' +
                ", idCriteria='" + idCriteria + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        CriterionSelfAssessmentDTO that = (CriterionSelfAssessmentDTO) obj;

        if (!idSelfAssessment.equals(that.idSelfAssessment)) return false;
        return idCriteria.equals(that.idCriteria);
    }
}
