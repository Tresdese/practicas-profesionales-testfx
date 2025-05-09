package logic.DTO;

import java.util.Date;

public class StudentProjectViewDTO {
    private int presentationId;
    private Date presentationDate;
    private String presentationType;
    private int projectId;
    private String projectName;
    private String studentMatricula;
    private String studentName;

    public StudentProjectViewDTO(int presentationId, Date presentationDate, String presentationType, int projectId,
                                 String projectName, String studentMatricula, String studentName) {
        this.presentationId = presentationId;
        this.presentationDate = presentationDate;
        this.presentationType = presentationType;
        this.projectId = projectId;
        this.projectName = projectName;
        this.studentMatricula = studentMatricula;
        this.studentName = studentName;
    }

    public int getPresentationId() {
        return presentationId;
    }

    public void setPresentationId(int presentationId) {
        this.presentationId = presentationId;
    }

    public Date getPresentationDate() {
        return presentationDate;
    }

    public void setPresentationDate(Date presentationDate) {
        this.presentationDate = presentationDate;
    }

    public String getPresentationType() {
        return presentationType;
    }

    public void setPresentationType(String presentationType) {
        this.presentationType = presentationType;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getStudentMatricula() {
        return studentMatricula;
    }

    public void setStudentMatricula(String studentMatricula) {
        this.studentMatricula = studentMatricula;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}