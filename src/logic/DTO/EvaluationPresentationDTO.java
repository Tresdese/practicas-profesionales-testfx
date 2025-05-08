package logic.DTO;

import java.util.Date;

public class EvaluationPresentationDTO {
    private int idEvaluation;
    private int idProject;
    private String tuiton;
    private Date date;
    private double average;

    public EvaluationPresentationDTO() {
        this.idEvaluation = 0;
        this.idProject = 0;
        this.tuiton = "";
        this.date = null;
        this.average = 0.0;
    }

    public EvaluationPresentationDTO(int idEvaluation, int idProject, String tuiton, Date date, double average) {
        this.idEvaluation = idEvaluation;
        this.idProject = idProject;
        this.tuiton = tuiton;
        this.date = date;
        this.average = average;
    }

    public EvaluationPresentationDTO(int idProject, String tuiton, Date date, double average) {
        this.idEvaluation = 0;
        this.idProject = idProject;
        this.tuiton = tuiton;
        this.date = date;
        this.average = average;
    }

    public int getIdEvaluation() {
        return idEvaluation;
    }

    public void setIdEvaluation(int idEvaluation) {
        this.idEvaluation = idEvaluation;
    }

    public int getIdProject() {
        return idProject;
    }

    public void setIdProject(int idProject) {
        this.idProject = idProject;
    }

    public String getTuiton() {
        return tuiton;
    }

    public void setTuiton(String tuiton) {
        this.tuiton = tuiton;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    @Override
    public String toString() {
        return "EvaluationPresentation{" +
                "idEvaluation=" + idEvaluation +
                ", idProject=" + idProject +
                ", tuiton='" + tuiton + '\'' +
                ", date=" + date +
                ", average=" + average +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        EvaluationPresentationDTO that = (EvaluationPresentationDTO) obj;

        if (idEvaluation != that.idEvaluation) return false;
        if (idProject != that.idProject) return false;
        if (Double.compare(that.average, average) != 0) return false;
        if (!tuiton.equals(that.tuiton)) return false;
        return date.equals(that.date);
    }
}