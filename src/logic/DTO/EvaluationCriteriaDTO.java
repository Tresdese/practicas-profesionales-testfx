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
}
