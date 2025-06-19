// src/logic/DTO/RepresentativeDTO.java
package logic.DTO;

public class RepresentativeDTO {
    private String idRepresentative;
    private String names;
    private String surnames;
    private String email;
    private String idOrganization;
    private String idDepartment;

    public RepresentativeDTO() {
        this.idRepresentative = "";
        this.names = "";
        this.surnames = "";
        this.email = "";
        this.idOrganization = "";
        this.idDepartment = "";
    }

    public RepresentativeDTO(String idRepresentative, String names, String surnames, String email, String idOrganization, String idDepartment) {
        this.idRepresentative = idRepresentative;
        this.names = names;
        this.surnames = surnames;
        this.email = email;
        this.idOrganization = idOrganization;
        this.idDepartment = idDepartment;
    }

    public String getIdRepresentative() { return idRepresentative; }
    public void setIdRepresentative(String idRepresentative) { this.idRepresentative = idRepresentative; }

    public String getNames() { return names; }
    public void setNames(String names) { this.names = names; }

    public String getSurnames() { return surnames; }
    public void setSurnames(String surnames) { this.surnames = surnames; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getIdOrganization() { return idOrganization; }
    public void setIdOrganization(String idOrganization) { this.idOrganization = idOrganization; }

    public String getIdDepartment() { return idDepartment; }
    public void setIdDepartment(String idDepartment) { this.idDepartment = idDepartment; }

    @Override
    public String toString() {
        return names + " " + surnames;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RepresentativeDTO that)) return false;
        return idRepresentative.equals(that.idRepresentative);
    }
}