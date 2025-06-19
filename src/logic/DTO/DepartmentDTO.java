package logic.DTO;

public class DepartmentDTO {
    private int departmentId;
    private String name;
    private String description;
    private int organizationId;

    public DepartmentDTO() {
        this.departmentId = 0;
        this.name = "";
        this.description = "";
        this.organizationId = 0;
    }

    public DepartmentDTO(int departmentId, String name, String description, int organizationId) {
        this.departmentId = departmentId;
        this.name = name;
        this.description = description;
        this.organizationId = organizationId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String toString() {
        return name;
    }
}