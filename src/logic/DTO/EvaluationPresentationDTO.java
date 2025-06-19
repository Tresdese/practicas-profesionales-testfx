package logic.DTO;

import java.util.Date;

public class EvaluationPresentationDTO {
    private int idEvaluation;
    private int idProject;
    private String tuition;
    private Date date;
    private String comment;
    private double average;

    public EvaluationPresentationDTO() {
        this.idEvaluation = 0;
        this.idProject = 0;
        this.tuition = "";
        this.date = null;
        this.comment = "";
        this.average = 0.0;
    }

    public EvaluationPresentationDTO(int idEvaluation, int idProject, String tuition, Date date, String comment, double average) {
        this.idEvaluation = idEvaluation;
        this.idProject = idProject;
        this.tuition = tuition;
        this.date = date;
        this.comment = comment;
        this.average = average;
    }

    public EvaluationPresentationDTO(int idProject, String tuition, Date date, String comment, double average) {
        this.idEvaluation = 0;
        this.idProject = idProject;
        this.tuition = tuition;
        this.date = date;
        this.comment = comment;
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

    public String getTuition() {
        return tuition;
    }

    public void setTuition(String tuition) {
        this.tuition = tuition;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment; // Getter para comentario
    }

    public void setComment(String comment) {
        this.comment = comment; // Setter para comentario
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
                ", tuition='" + tuition + '\'' +
                ", date=" + date +
                ", comment='" + comment + '\'' +
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
        if (!tuition.equals(that.tuition)) return false;
        if (!date.equals(that.date)) return false;
        return comment.equals(that.comment);
    }
}