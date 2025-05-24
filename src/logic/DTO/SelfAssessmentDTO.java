package logic.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SelfAssessmentDTO {
    private int selfAssessmentId;
    private String comments;
    private BigDecimal grade;
    private String registration;
    private int projectId;
    private Integer evidenceId;
    private LocalDate registrationDate;
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
        this.grade = BigDecimal.ZERO;
        this.registration = "";
        this.projectId = 0;
        this.evidenceId = null;
        this.registrationDate = null;
        this.status = EstadoAutoevaluacion.COMPLETADA;
        this.generalComments = "";
    }

    public SelfAssessmentDTO(int selfAssessmentId, String comments, BigDecimal grade, String registration, int projectId, Integer evidenceId, LocalDate registrationDate, EstadoAutoevaluacion status, String generalComments) {
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
    public BigDecimal getGrade() { return grade; }
    public void setGrade(BigDecimal grade) { this.grade = grade; }
    public String getRegistration() { return registration; }
    public void setRegistration(String registration) { this.registration = registration; }
    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }
    public Integer getEvidenceId() { return evidenceId; }
    public void setEvidenceId(Integer evidenceId) { this.evidenceId = evidenceId; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
    public EstadoAutoevaluacion getStatus() { return status; }
    public void setStatus(EstadoAutoevaluacion status) { this.status = status; }
    public String getGeneralComments() { return generalComments; }
    public void setGeneralComments(String generalComments) { this.generalComments = generalComments; }

    @Override
    public String toString() {
        return "SelfAssessmentDTO{" +
                "selfAssessmentId=" + selfAssessmentId +
                ", comments='" + comments + '\'' +
                ", grade=" + grade +
                ", registration='" + registration + '\'' +
                ", projectId=" + projectId +
                ", evidenceId=" + evidenceId +
                ", registrationDate=" + registrationDate +
                ", status=" + status +
                ", generalComments='" + generalComments + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SelfAssessmentDTO that = (SelfAssessmentDTO) obj;
        return selfAssessmentId == that.selfAssessmentId &&
                projectId == that.projectId &&
                (grade == null ? that.grade == null : grade.compareTo(that.grade) == 0) &&
                (evidenceId == null ? that.evidenceId == null : evidenceId.equals(that.evidenceId)) &&
                (registrationDate == null ? that.registrationDate == null : registrationDate.equals(that.registrationDate)) &&
                (status == null ? that.status == null : status.equals(that.status)) &&
                (comments == null ? that.comments == null : comments.equals(that.comments)) &&
                (registration == null ? that.registration == null : registration.equals(that.registration)) &&
                (generalComments == null ? that.generalComments == null : generalComments.equals(that.generalComments));
    }
}

