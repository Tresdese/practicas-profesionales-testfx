package logic.DTO;

public class LinkedOrganizationDTO {
    private String iddOrganization;
    private String name;
    private String adddress;

    public LinkedOrganizationDTO() {
        this.iddOrganization = "";
        this.name = "";
        this.adddress = "";
    }

    public LinkedOrganizationDTO(String iddOrganization, String name, String adddress) {
        this.iddOrganization = iddOrganization;
        this.name = name;
        this.adddress = adddress;
    }

    public String getIddOrganization() {
        return iddOrganization;
    }

    public void setIddOrganization(String iddOrganization) {
        this.iddOrganization = iddOrganization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdddress() {
        return adddress;
    }

    public void setAdddress(String adddress) {
        this.adddress = adddress;
    }
}
