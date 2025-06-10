package logic.DTO;

public class PartialEvaluationDTO {
    private String idEvaluation;
    private double average;
    private String tuition;
    private String evidence;

    public PartialEvaluationDTO() {
        this.idEvaluation = "";
        this.average = 0;
        this.tuition = "";
        this.evidence = "";
    }

    public PartialEvaluationDTO(String idEvaluation, double average, String tuition, String evidence) {
        this.idEvaluation = idEvaluation;
        this.average = average;
        this.tuition = tuition;
        this.evidence = evidence;
    }

    public String getIdEvaluation() {
        return idEvaluation;
    }

    public void setIdEvaluation(String idEvaluation) {
        this.idEvaluation = idEvaluation;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public String getTuition() {
        return tuition;
    }

    public void setTuition(String tuition) {
        this.tuition = tuition;
    }

    public String getEvidence() {
        return evidence;
    }
    
    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    @Override
    public String toString() {
        return "PartialEvaluationDTO{" +
                "idEvaluation='" + idEvaluation + '\'' +
                ", average=" + average +
                ", tuiton='" + tuition + '\'' +
                ", evidence='" + evidence + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        PartialEvaluationDTO that = (PartialEvaluationDTO) obj;

        if (Double.compare(that.average, average) != 0) return false;
        if (!idEvaluation.equals(that.idEvaluation)) return false;
        if (!tuition.equals(that.tuition)) return false;
        return evidence.equals(that.evidence);
    }
}
