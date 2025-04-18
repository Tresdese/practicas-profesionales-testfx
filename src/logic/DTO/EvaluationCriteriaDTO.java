package logic.DTO;

public class EvaluationCriteriaDTO {
    private String idEvaluation;
    private String idCriterion;

    public EvaluationCriteriaDTO() {
        this.idEvaluation = "";
        this.idCriterion = "";
    }

    public EvaluationCriteriaDTO(String idEvaluation, String idCriterion) {
        this.idEvaluation = idEvaluation;
        this.idCriterion = idCriterion;
    }

    public String getIdEvaluation() {
        return idEvaluation;
    }

    public void setIdEvaluation(String idEvaluation) {
        this.idEvaluation = idEvaluation;
    }

    public String getIdCriterion() {
        return idCriterion;
    }

    public void setIdCriterion(String idCriterion) {
        this.idCriterion = idCriterion;
    }

    @Override
    public String toString() {
        return "EvaluationCriteriaDTO{" +
                "idEvaluation='" + idEvaluation + '\'' +
                ", idCriterion='" + idCriterion + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        EvaluationCriteriaDTO that = (EvaluationCriteriaDTO) obj;

        if (!idEvaluation.equals(that.idEvaluation)) return false;
        return idCriterion.equals(that.idCriterion);
    }
}
