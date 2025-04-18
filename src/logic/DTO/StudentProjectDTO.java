package logic.DTO;

public class StudentProjectDTO {
    private String idProject;
    private String tuiton;

    public StudentProjectDTO() {
        this.idProject = "";
        this.tuiton = "";
    }

    public StudentProjectDTO(String idProject, String tuiton) {
        this.idProject = idProject;
        this.tuiton = tuiton;
    }

    public String getIdProject() {
        return idProject;
    }

    public void setIdProject(String idProject) {
        this.idProject = idProject;
    }

    public String getTuiton() {
        return tuiton;
    }

    public void setTuiton(String tuiton) {
        this.tuiton = tuiton;
    }

    @Override
    public String toString() {
        return "StudentProjectDTO{" +
                "idProject='" + idProject + '\'' +
                ", tuiton='" + tuiton + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        StudentProjectDTO that = (StudentProjectDTO) obj;

        if (!idProject.equals(that.idProject)) return false;
        return tuiton.equals(that.tuiton);
    }
}
