package logic.DTO;

import java.util.Date;

public class SelfAssessmentDTO {
    private int selfAssessmentId;
    private String comments;
    private float grade;
    private String registration;
    private int projectId;
    private int evidenceId;
    private Date registrationDate;
    private EstadoAutoevaluacion status;
    private String generalComments;

    public enum EstadoAutoevaluacion {
        PENDIENTE("pendiente"),
        COMPLETADA("completada");

        private final String value;
        EstadoAutoevaluacion(String value) { this.value = value; }
        public String getValue() { return value; }
        public static EstadoAutoevaluacion fromString(String value) {
            for (EstadoAutoevaluacion estado : EstadoAutoevaluacion.values()) {
                if (estado.value.equalsIgnoreCase(value)) {
                    return estado;
                }
            }
            throw new IllegalArgumentException("Estado desconocido: " + value);
        }
    }

    public SelfAssessmentDTO() {
        this.selfAssessmentId = 0;
        this.comments = "";
        this.grade = 0.0f;
        this.registration = "";
        this.projectId = 0;
        this.evidenceId = 0;
        this.registrationDate = null;
        this.status = EstadoAutoevaluacion.COMPLETADA;
        this.generalComments = "";
    }

    public SelfAssessmentDTO(int selfAssessmentId, String comments, float grade, String registration, int projectId, int evidenceId, Date registrationDate, EstadoAutoevaluacion status, String generalComments) {
        this.selfAssessmentId = selfAssessmentId;
        this.comments = comments;
        this.grade = grade;
        this.registration = registration;
        this.projectId = projectId;
        this.evidenceId = evidenceId;
        this.registrationDate = registrationDate;
        this.status = status;
        this.generalComments = generalComments;
    }

    public int getSelfAssessmentId() { return selfAssessmentId; }
    public void setSelfAssessmentId(int selfAssessmentId) { this.selfAssessmentId = selfAssessmentId; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public float getGrade() { return grade; }
    public void setGrade(float grade) { this.grade = grade; }

    public String getRegistration() { return registration; }
    public void setRegistration(String registration) { this.registration = registration; }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public int getEvidenceId() { return evidenceId; }
    public void setEvidenceId(int evidenceId) { this.evidenceId = evidenceId; }

    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }

    public EstadoAutoevaluacion getStatus() { return status; }
    public void setStatus(EstadoAutoevaluacion status) { this.status = status; }

    public String getGeneralComments() { return generalComments; }
    public void setGeneralComments(String generalComments) { this.generalComments = generalComments; }
}