package logic.DTO;

import java.sql.Timestamp;

public class ProjectPresentationDTO {
    private int idPresentation;
    private String idProject;
    private Timestamp date;
    private Type type;

    public ProjectPresentationDTO() {
        this.idPresentation = 0;
        this.idProject = "";
        this.date = null;
        this.type = Type.Partial;
    }

    public ProjectPresentationDTO(int idPresentation, String idProject, Timestamp date, Type type) {
        this.idPresentation = idPresentation;
        this.idProject = idProject;
        this.date = date;
        this.type = type;
    }

    public ProjectPresentationDTO (String idProject, Timestamp date, Type type) {
        this.idPresentation = 0;
        this.idProject = idProject;
        this.date = date;
        this.type = type;
    }

    public int getIdPresentation() {
        return idPresentation;
    }

    public void setIdPresentation(int idPresentation) {
        this.idPresentation = idPresentation;
    }

    public String getIdProject() {
        return idProject;
    }

    public void setIdProject(String idProject) {
        this.idProject = idProject;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ProjectPresentationDTO{" +
                "idPresentation=" + idPresentation +
                ", idProject='" + idProject + '\'' +
                ", date=" + date +
                ", tipe='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectPresentationDTO)) return false;
        ProjectPresentationDTO that = (ProjectPresentationDTO) o;
        return idPresentation == that.idPresentation && idProject.equals(that.idProject) && date.equals(that.date) && type.equals(that.type);
    }
}
