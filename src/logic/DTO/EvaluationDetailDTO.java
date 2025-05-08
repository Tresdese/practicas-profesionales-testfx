package logic.DTO;

public class EvaluationDetailDTO {
    private int idDetail;
    private int idEvaluation;
    private int idCriteria;
    private double grade;

    public EvaluationDetailDTO() {
        this.idDetail = 0;
        this.idEvaluation = 0;
        this.idCriteria = 0;
        this.grade = 0.0;
    }

    public EvaluationDetailDTO(int idDetail, int idEvaluation, int idCriteria, double grade) {
        this.idDetail = idDetail;
        this.idEvaluation = idEvaluation;
        this.idCriteria = idCriteria;
        this.grade = grade;
    }

    public EvaluationDetailDTO(int idEvaluation, int idCriteria, double grade) {
        this.idDetail = 0;
        this.idEvaluation = idEvaluation;
        this.idCriteria = idCriteria;
        this.grade = grade;
    }

    public int getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(int idDetail) {
        this.idDetail = idDetail;
    }

    public int getIdEvaluation() {
        return idEvaluation;
    }

    public void setIdEvaluation(int idEvaluation) {
        this.idEvaluation = idEvaluation;
    }

    public int getIdCriteria() {
        return idCriteria;
    }

    public void setIdCriteria(int idCriteria) {
        this.idCriteria = idCriteria;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "EvaluationDetail{" +
                "idDetail=" + idDetail +
                ", idEvaluation=" + idEvaluation +
                ", idCriteria=" + idCriteria +
                ", grade=" + grade +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EvaluationDetailDTO that = (EvaluationDetailDTO) obj;
        return idDetail == that.idDetail &&
                idEvaluation == that.idEvaluation &&
                idCriteria == that.idCriteria &&
                Double.compare(that.grade, grade) == 0;
    }
}
