package logic.DTO;

import java.sql.Timestamp;

public class ProjectPresentationDTO {
    private int idPresentation;
    private String idProject;
    private Timestamp date;
    private Tipe tipe;

    public ProjectPresentationDTO() {
        this.idPresentation = 0;
        this.idProject = "";
        this.date = null;
        this.tipe = Tipe.None;
    }

    public ProjectPresentationDTO(int idPresentation, String idProject, Timestamp date, Tipe tipe) {
        this.idPresentation = idPresentation;
        this.idProject = idProject;
        this.date = date;
        this.tipe = tipe;
    }

    public ProjectPresentationDTO (String idProject, Timestamp date, Tipe tipe) {
        this.idPresentation = 0;
        this.idProject = idProject;
        this.date = date;
        this.tipe = tipe;
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

    public Tipe getTipe() {
        return tipe;
    }

    public void setTipe(Tipe tipe) {
        this.tipe = tipe;
    }

    @Override
    public String toString() {
        return "ProjectPresentationDTO{" +
                "idPresentation=" + idPresentation +
                ", idProject='" + idProject + '\'' +
                ", date=" + date +
                ", tipe='" + tipe + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectPresentationDTO)) return false;
        ProjectPresentationDTO that = (ProjectPresentationDTO) o;
        return idPresentation == that.idPresentation && idProject.equals(that.idProject) && date.equals(that.date) && tipe.equals(that.tipe);
    }
}
