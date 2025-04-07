package logic.DTO;

public class PartialEvaluationDTO {
    private String idEvaluation;
    private double average;
    private String tuiton;
    private String evidence;

    public PartialEvaluationDTO() {
        this.idEvaluation = "";
        this.average = 0;
        this.tuiton = "";
        this.evidence = "";
    }

    public PartialEvaluationDTO(String idEvaluation, double average, String tuiton, String evidence) {
        this.idEvaluation = idEvaluation;
        this.average = average;
        this.tuiton = tuiton;
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

    public String getTuiton() {
        return tuiton;
    }

    public void setTuiton(String tuiton) {
        this.tuiton = tuiton;
    }
}
