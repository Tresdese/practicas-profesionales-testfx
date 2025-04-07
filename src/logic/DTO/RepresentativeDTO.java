package logic.DTO;

public class RepresentativeDTO {
    private String idRepresentative;
    private String names;
    private String surnames;
    private String email;
    private String idOrganization;

    public RepresentativeDTO() {
        this.idRepresentative = "";
        this.names = "";
        this.surnames = "";
        this.email = "";
        this.idOrganization = "";
    }

    public RepresentativeDTO(String idRepresentative, String names, String surnames, String email, String idOrganization) {
        this.idRepresentative = idRepresentative;
        this.names = names;
        this.surnames = surnames;
        this.email = email;
        this.idOrganization = idOrganization;
    }

    public String getIdRepresentative() {
        return idRepresentative;
    }

    public void setIdRepresentative(String idRepresentative) {
        this.idRepresentative = idRepresentative;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getSurnames() {
        return surnames;
    }

    public void setSurnames(String surnames) {
        this.surnames = surnames;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdOrganization() {
        return idOrganization;
    }

    public void setIdOrganization(String idOrganization) {
        this.idOrganization = idOrganization;
    }
}
