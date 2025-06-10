package logic.DTO;

public class StudentProjectDTO {
    private String idProject;
    private String tuition;

    public StudentProjectDTO() {
        this.idProject = "";
        this.tuition = "";
    }

    public StudentProjectDTO(String idProject, String tuition) {
        this.idProject = idProject;
        this.tuition = tuition;
    }

    public String getIdProject() {
        return idProject;
    }

    public void setIdProject(String idProject) {
        this.idProject = idProject;
    }

    public String getTuition() {
        return tuition;
    }

    public void setTuition(String tuition) {
        this.tuition = tuition;
    }

    @Override
    public String toString() {
        return "StudentProjectDTO{" +
                "idProject='" + idProject + '\'' +
                ", tuiton='" + tuition + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        StudentProjectDTO that = (StudentProjectDTO) obj;

        if (!idProject.equals(that.idProject)) return false;
        return tuition.equals(that.tuition);
    }
}
