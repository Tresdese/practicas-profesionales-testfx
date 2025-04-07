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
}
